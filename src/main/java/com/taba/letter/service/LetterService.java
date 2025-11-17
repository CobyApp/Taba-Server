package com.taba.letter.service;

import com.taba.common.exception.BusinessException;
import com.taba.common.exception.ErrorCode;
import com.taba.common.util.SecurityUtil;
import com.taba.letter.dto.LetterCreateRequest;
import com.taba.letter.dto.LetterDto;
import com.taba.letter.entity.Letter;
import com.taba.letter.entity.LetterImage;
import com.taba.letter.entity.LetterLike;
import com.taba.letter.entity.LetterSave;
import com.taba.letter.repository.LetterLikeRepository;
import com.taba.letter.repository.LetterRepository;
import com.taba.letter.repository.LetterSaveRepository;
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
    private final LetterLikeRepository letterLikeRepository;
    private final LetterSaveRepository letterSaveRepository;
    private final UserRepository userRepository;

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
                .flowerType(request.getFlowerType())
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
        }

        letter = letterRepository.save(letter);
        return toDto(letter, sender.getId());
    }

    @Transactional(readOnly = true)
    public Page<LetterDto> getPublicLetters(Pageable pageable) {
        Page<Letter> letters = letterRepository.findPublicLetters(pageable);
        String currentUserId = SecurityUtil.getCurrentUserId();
        return letters.map(letter -> {
            letter.incrementViews();
            letterRepository.save(letter);
            return toDto(letter, currentUserId);
        });
    }

    @Transactional(readOnly = true)
    public LetterDto getLetter(String letterId) {
        Letter letter = letterRepository.findActiveById(letterId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LETTER_NOT_FOUND));

        String currentUserId = SecurityUtil.getCurrentUserId();
        checkLetterAccess(letter, currentUserId);

        letter.incrementViews();
        letterRepository.save(letter);

        return toDto(letter, currentUserId);
    }

    @Transactional
    public LetterDto toggleLike(String letterId) {
        Letter letter = letterRepository.findActiveById(letterId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LETTER_NOT_FOUND));

        User user = SecurityUtil.getCurrentUser();
        if (user == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        LetterLike existingLike = letterLikeRepository.findByLetterIdAndUserId(letterId, user.getId()).orElse(null);

        if (existingLike != null) {
            letterLikeRepository.delete(existingLike);
            letter.decrementLikes();
        } else {
            LetterLike like = new LetterLike(letter, user);
            letterLikeRepository.save(like);
            letter.incrementLikes();
        }

        letterRepository.save(letter);
        return toDto(letter, user.getId());
    }

    @Transactional
    public LetterDto toggleSave(String letterId) {
        Letter letter = letterRepository.findActiveById(letterId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LETTER_NOT_FOUND));

        User user = SecurityUtil.getCurrentUser();
        if (user == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        LetterSave existingSave = letterSaveRepository.findByLetterIdAndUserId(letterId, user.getId()).orElse(null);

        if (existingSave != null) {
            letterSaveRepository.delete(existingSave);
            letter.decrementSavedCount();
        } else {
            LetterSave save = new LetterSave(letter, user);
            letterSaveRepository.save(save);
            letter.incrementSavedCount();
        }

        letterRepository.save(letter);
        return toDto(letter, user.getId());
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

        Boolean isLiked = currentUserId != null && 
                letterLikeRepository.existsByLetterIdAndUserId(letter.getId(), currentUserId);
        Boolean isSaved = currentUserId != null && 
                letterSaveRepository.existsByLetterIdAndUserId(letter.getId(), currentUserId);

        return LetterDto.builder()
                .id(letter.getId())
                .title(letter.getTitle())
                .content(letter.getContent())
                .preview(letter.getPreview())
                .sender(com.taba.user.dto.UserMapper.INSTANCE.toDto(letter.getSender()))
                .flowerType(letter.getFlowerType())
                .visibility(letter.getVisibility())
                .isAnonymous(letter.getIsAnonymous())
                .sentAt(letter.getSentAt())
                .likes(letter.getLikes())
                .views(letter.getViews())
                .savedCount(letter.getSavedCount())
                .isLiked(isLiked)
                .isSaved(isSaved)
                .attachedImages(images)
                .build();
    }
}

