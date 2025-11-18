package com.taba.letter.service;

import com.taba.common.exception.BusinessException;
import com.taba.common.exception.ErrorCode;
import com.taba.common.util.SecurityUtil;
import com.taba.letter.dto.LetterCreateRequest;
import com.taba.letter.dto.LetterDto;
import com.taba.letter.entity.Letter;
import com.taba.letter.entity.LetterImage;
import com.taba.letter.entity.LetterRecipient;
import com.taba.letter.repository.LetterRepository;
import com.taba.letter.repository.LetterRecipientRepository;
import com.taba.letter.repository.LetterReportRepository;
import com.taba.friendship.service.FriendshipService;
import com.taba.notification.service.NotificationService;
import com.taba.user.entity.User;
import com.taba.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class LetterService {

    private final LetterRepository letterRepository;
    private final LetterRecipientRepository letterRecipientRepository;
    private final LetterReportRepository letterReportRepository;
    private final UserRepository userRepository;
    private final FriendshipService friendshipService;
    private final NotificationService notificationService;

    @Transactional
    public LetterDto createLetter(LetterCreateRequest request) {
        User sender = SecurityUtil.getCurrentUser();
        if (sender == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        User recipient = null;
        if (request.getRecipientId() != null) {
            recipient = userRepository.findActiveUserById(request.getRecipientId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        }

        Letter letter = Letter.builder()
                .sender(sender)
                .recipient(recipient)
                .title(request.getTitle())
                .content(request.getContent())
                .preview(request.getPreview())
                .visibility(request.getVisibility())
                .isAnonymous(request.getIsAnonymous())
                .templateBackground(request.getTemplate() != null ? request.getTemplate().getBackground() : null)
                .templateTextColor(request.getTemplate() != null ? request.getTemplate().getTextColor() : null)
                .templateFontFamily(request.getTemplate() != null ? request.getTemplate().getFontFamily() : null)
                .templateFontSize(request.getTemplate() != null ? request.getTemplate().getFontSize() : null)
                .scheduledAt(request.getScheduledAt())
                .build();

        if (request.getAttachedImages() != null) {
            for (int i = 0; i < request.getAttachedImages().size(); i++) {
                letter.addImage(new LetterImage(request.getAttachedImages().get(i), i));
            }
        }

        if (request.getScheduledAt() == null || request.getScheduledAt().isBefore(LocalDateTime.now())) {
            letter.send();
            
            // 즉시 발송된 편지인 경우 알림 발송 (FCM 푸시 포함)
            if (recipient != null) {
                notificationService.createAndSendNotification(
                        recipient,
                        "새로운 편지가 도착했습니다",
                        letter.getTitle(),
                        com.taba.notification.entity.Notification.NotificationCategory.LETTER,
                        letter.getId()
                );
            }
        }

        letter = letterRepository.save(letter);
        return toDto(letter, sender.getId());
    }

    /**
     * 편지 답장 생성 (답장 시 자동으로 친구 추가)
     */
    @Transactional
    public LetterDto replyLetter(String originalLetterId, LetterCreateRequest request) {
        User sender = SecurityUtil.getCurrentUser();
        if (sender == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        // 원본 편지 조회
        Letter originalLetter = letterRepository.findActiveById(originalLetterId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LETTER_NOT_FOUND));

        // 원본 편지의 sender가 recipient가 됨
        User recipient = originalLetter.getSender();
        if (recipient == null) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        // 자기 자신에게 답장 불가
        if (recipient.getId().equals(sender.getId())) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        // 친구가 아니면 자동으로 친구 추가
        friendshipService.addFriendByUserIds(sender.getId(), recipient.getId());

        // 답장 편지 생성
        Letter replyLetter = Letter.builder()
                .sender(sender)
                .recipient(recipient)
                .title(request.getTitle())
                .content(request.getContent())
                .preview(request.getPreview())
                .visibility(Letter.Visibility.DIRECT) // 답장은 항상 DIRECT
                .isAnonymous(request.getIsAnonymous() != null ? request.getIsAnonymous() : false)
                .templateBackground(request.getTemplate() != null ? request.getTemplate().getBackground() : null)
                .templateTextColor(request.getTemplate() != null ? request.getTemplate().getTextColor() : null)
                .templateFontFamily(request.getTemplate() != null ? request.getTemplate().getFontFamily() : null)
                .templateFontSize(request.getTemplate() != null ? request.getTemplate().getFontSize() : null)
                .scheduledAt(request.getScheduledAt())
                .build();

        if (request.getAttachedImages() != null) {
            for (int i = 0; i < request.getAttachedImages().size(); i++) {
                replyLetter.addImage(new LetterImage(request.getAttachedImages().get(i), i));
            }
        }

        if (request.getScheduledAt() == null || request.getScheduledAt().isBefore(LocalDateTime.now())) {
            replyLetter.send();
            
            // 즉시 발송된 답장인 경우 알림 발송 (FCM 푸시 포함)
            notificationService.createAndSendNotification(
                    recipient,
                    "새로운 편지가 도착했습니다",
                    replyLetter.getTitle(),
                    com.taba.notification.entity.Notification.NotificationCategory.LETTER,
                    replyLetter.getId()
            );
        }

        replyLetter = letterRepository.save(replyLetter);
        return toDto(replyLetter, sender.getId());
    }

    @Transactional(readOnly = true)
    public Page<LetterDto> getPublicLetters(Pageable pageable) {
        String currentUserId = SecurityUtil.getCurrentUserId();
        Page<Letter> letters;
        
        // 로그인한 사용자의 경우 자신이 작성한 편지 제외
        if (currentUserId != null && !currentUserId.isEmpty()) {
            letters = letterRepository.findPublicLettersExcludingUser(currentUserId, pageable);
        } else {
            // 비로그인 사용자는 모든 공개 편지 조회
            letters = letterRepository.findPublicLetters(pageable);
        }
        
        return letters.map(letter -> {
            letter.incrementViews();
            letterRepository.save(letter);
            return toDto(letter, currentUserId);
        });
    }

    @Transactional
    public LetterDto getLetter(String letterId) {
        Letter letter = letterRepository.findActiveById(letterId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LETTER_NOT_FOUND));

        String currentUserId = SecurityUtil.getCurrentUserId();
        checkLetterAccess(letter, currentUserId);

        letter.incrementViews();
        
        // 공개 편지인 경우 LetterRecipient로 읽음 처리
        if (letter.getVisibility() == Letter.Visibility.PUBLIC && currentUserId != null && !currentUserId.isEmpty()) {
            User currentUser = userRepository.findActiveUserById(currentUserId)
                    .orElse(null);
            
            if (currentUser != null && !letter.getSender().getId().equals(currentUserId)) {
                // 이미 LetterRecipient가 있는지 확인
                LetterRecipient letterRecipient = letterRecipientRepository
                        .findByLetterIdAndUserId(letterId, currentUserId)
                        .orElse(null);
                
                if (letterRecipient == null) {
                    // 새로운 LetterRecipient 생성
                    letterRecipient = LetterRecipient.builder()
                            .letter(letter)
                            .user(currentUser)
                            .build();
                    letterRecipient = letterRecipientRepository.save(letterRecipient);
                }
                
                // 읽지 않은 경우 읽음 처리
                if (letterRecipient.getIsRead() == null || !letterRecipient.getIsRead()) {
                    letterRecipient.markAsRead();
                    letterRecipientRepository.save(letterRecipient);
                }
            }
        } else if (letter.getVisibility() == Letter.Visibility.DIRECT) {
            // DIRECT 편지의 경우 기존 로직 유지 (recipient 필드 사용)
            if (letter.getRecipient() != null && 
                letter.getRecipient().getId().equals(currentUserId) && 
                (letter.getIsRead() == null || !letter.getIsRead())) {
                letter.markAsRead();
            }
        }
        
        letterRepository.save(letter);

        return toDto(letter, currentUserId);
    }

    @Transactional
    public void reportLetter(String letterId, String reason) {
        Letter letter = letterRepository.findActiveById(letterId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LETTER_NOT_FOUND));

        User reporter = SecurityUtil.getCurrentUser();
        if (reporter == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        // 이미 신고한 경우 확인
        if (letterReportRepository.existsByLetterIdAndReporterId(letterId, reporter.getId())) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        com.taba.letter.entity.LetterReport report = new com.taba.letter.entity.LetterReport(
                letter, reporter, reason);
        letterReportRepository.save(report);
    }

    @Transactional
    public void deleteLetter(String letterId) {
        Letter letter = letterRepository.findActiveById(letterId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LETTER_NOT_FOUND));

        User user = SecurityUtil.getCurrentUser();
        if (user == null || !letter.getSender().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        letter.softDelete();
        letterRepository.save(letter);
    }

    private void checkLetterAccess(Letter letter, String currentUserId) {
        if (letter.getVisibility() == Letter.Visibility.PRIVATE) {
            if (currentUserId == null || !letter.getSender().getId().equals(currentUserId)) {
                throw new BusinessException(ErrorCode.FORBIDDEN);
            }
        } else if (letter.getVisibility() == Letter.Visibility.DIRECT) {
            if (currentUserId == null || 
                (!letter.getSender().getId().equals(currentUserId) && 
                 (letter.getRecipient() == null || !letter.getRecipient().getId().equals(currentUserId)))) {
                throw new BusinessException(ErrorCode.FORBIDDEN);
            }
        }
    }

    private LetterDto toDto(Letter letter, String currentUserId) {
        List<String> images = letter.getImages().stream()
                .sorted((a, b) -> a.getImageOrder().compareTo(b.getImageOrder()))
                .map(LetterImage::getImageUrl)
                .collect(Collectors.toList());

        // 템플릿 정보 구성
        com.taba.letter.dto.LetterTemplateDto template = null;
        if (letter.getTemplateBackground() != null || 
            letter.getTemplateTextColor() != null || 
            letter.getTemplateFontFamily() != null || 
            letter.getTemplateFontSize() != null) {
            template = com.taba.letter.dto.LetterTemplateDto.builder()
                    .background(letter.getTemplateBackground())
                    .textColor(letter.getTemplateTextColor())
                    .fontFamily(letter.getTemplateFontFamily())
                    .fontSize(letter.getTemplateFontSize())
                    .build();
        }

        return LetterDto.builder()
                .id(letter.getId())
                .title(letter.getTitle())
                .content(letter.getContent())
                .preview(letter.getPreview())
                .sender(com.taba.user.dto.UserMapper.INSTANCE.toDto(letter.getSender()))
                .visibility(letter.getVisibility())
                .isAnonymous(letter.getIsAnonymous())
                .sentAt(letter.getSentAt())
                .views(letter.getViews())
                .attachedImages(images)
                .template(template)
                .build();
    }
}

