package com.taba.friendship.service;

import com.taba.common.exception.BusinessException;
import com.taba.common.exception.ErrorCode;
import com.taba.common.util.SecurityUtil;
import com.taba.friendship.entity.Friendship;
import com.taba.friendship.repository.FriendshipRepository;
import com.taba.letter.entity.Letter;
import com.taba.letter.entity.LetterRecipient;
import com.taba.letter.repository.LetterRepository;
import com.taba.letter.repository.LetterRecipientRepository;
import com.taba.invite.entity.InviteCode;
import com.taba.invite.repository.InviteCodeRepository;
import com.taba.user.entity.User;
import com.taba.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final LetterRepository letterRepository;
    private final LetterRecipientRepository letterRecipientRepository;
    private final UserRepository userRepository;
    private final InviteCodeRepository inviteCodeRepository;

    @Transactional
    public com.taba.friendship.dto.AddFriendResponse addFriend(String inviteCode) {
        User currentUser = SecurityUtil.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        // 초대 코드 형식 검증 (6자리 고정)
        if (inviteCode == null || inviteCode.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INVITE_CODE);
        }
        String trimmedCode = inviteCode.trim().toUpperCase();
        if (trimmedCode.length() != 6) {
            throw new BusinessException(ErrorCode.INVALID_INVITE_CODE);
        }

        // InviteCode 검증
        InviteCode code = inviteCodeRepository.findByCode(trimmedCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_INVITE_CODE));

        if (code.isExpired()) {
            throw new BusinessException(ErrorCode.INVITE_CODE_EXPIRED);
        }

        if (code.isUsed()) {
            throw new BusinessException(ErrorCode.INVITE_CODE_ALREADY_USED);
        }

        User friendUser = code.getUser();
        
        // 자기 자신의 초대 코드인 경우 친구 정보와 함께 반환
        if (friendUser.getId().equals(currentUser.getId())) {
            com.taba.user.dto.UserDto friendDto = com.taba.user.dto.UserMapper.INSTANCE.toDto(friendUser);
            return com.taba.friendship.dto.AddFriendResponse.builder()
                    .friend(friendDto)
                    .alreadyFriends(false)
                    .isOwnCode(true)
                    .build();
        }
        
        // 이미 친구인지 확인
        boolean alreadyFriends = friendshipRepository.existsByUserIdAndFriendIdAndDeletedAtIsNull(
                currentUser.getId(), friendUser.getId());

        // 이미 친구인 경우 친구 정보만 반환
        if (alreadyFriends) {
            com.taba.user.dto.UserDto friendDto = com.taba.user.dto.UserMapper.INSTANCE.toDto(friendUser);
            return com.taba.friendship.dto.AddFriendResponse.builder()
                    .friend(friendDto)
                    .alreadyFriends(true)
                    .isOwnCode(false)
                    .build();
        }

        // 양방향 친구 관계 생성
        Friendship friendship1 = Friendship.builder()
                .user(currentUser)
                .friend(friendUser)
                .build();
        friendshipRepository.save(friendship1);

        Friendship friendship2 = Friendship.builder()
                .user(friendUser)
                .friend(currentUser)
                .build();
        friendshipRepository.save(friendship2);

        // 초대 코드 사용 처리
        code.use(currentUser);
        inviteCodeRepository.save(code);

        // 친구 정보 반환
        com.taba.user.dto.UserDto friendDto = com.taba.user.dto.UserMapper.INSTANCE.toDto(friendUser);
        return com.taba.friendship.dto.AddFriendResponse.builder()
                .friend(friendDto)
                .alreadyFriends(false)
                .isOwnCode(false)
                .build();
    }

    @Transactional(readOnly = true)
    public List<com.taba.user.dto.UserDto> getFriends() {
        User currentUser = SecurityUtil.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        List<Friendship> friendships = friendshipRepository.findByUserId(currentUser.getId());
        return friendships.stream()
                .map(friendship -> com.taba.user.dto.UserMapper.INSTANCE.toDto(friendship.getFriend()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<com.taba.friendship.dto.SharedFlowerDto> getFriendLetters(
            String friendId, org.springframework.data.domain.Pageable pageable) {
        User currentUser = SecurityUtil.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        // 친구 관계 확인
        friendshipRepository.findByUserIds(currentUser.getId(), friendId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FRIENDSHIP_NOT_FOUND));

        // 친구 간 주고받은 편지 조회 (페이지네이션 없이 전체 조회 후 정렬)
        // 공개편지의 sentAt 조정을 위해 전체를 가져온 후 메모리에서 정렬
        org.springframework.data.domain.Pageable allPageable = PageRequest.of(0, Integer.MAX_VALUE, 
                Sort.by(Sort.Direction.ASC, "sentAt"));
        org.springframework.data.domain.Page<Letter> allLetters = 
                letterRepository.findLettersBetweenFriends(currentUser.getId(), friendId, allPageable);

        // 공개편지의 조정된 sentAt을 계산하여 정렬
        List<Letter> sortedLetters = allLetters.getContent().stream()
                .sorted((l1, l2) -> {
                    LocalDateTime sentAt1 = getAdjustedSentAt(l1, currentUser.getId(), friendId);
                    LocalDateTime sentAt2 = getAdjustedSentAt(l2, currentUser.getId(), friendId);
                    if (sentAt1 == null && sentAt2 == null) return 0;
                    if (sentAt1 == null) return 1;
                    if (sentAt2 == null) return -1;
                    return sentAt1.compareTo(sentAt2);
                })
                .collect(Collectors.toList());

        // 페이지네이션 적용
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), sortedLetters.size());
        List<Letter> pagedLetters = start < sortedLetters.size() 
                ? sortedLetters.subList(start, end) 
                : Collections.emptyList();

        // Page 객체 생성
        org.springframework.data.domain.Page<Letter> letters = new PageImpl<>(
                pagedLetters, pageable, sortedLetters.size());

        return letters.map(letter -> {
            try {
                // LetterSummaryDto 생성
                com.taba.friendship.dto.LetterSummaryDto letterSummary = 
                        com.taba.friendship.dto.LetterSummaryDto.builder()
                                .id(letter.getId())
                                .title(letter.getTitle() != null ? letter.getTitle() : "")
                                .preview(letter.getPreview() != null ? letter.getPreview() : "")
                                .fontFamily(letter.getTemplateFontFamily() != null ? letter.getTemplateFontFamily() : null)
                                .build();

                // sentByMe 확인 (sender는 nullable=false이므로 항상 존재해야 함)
                boolean sentByMe = false;
                try {
                    User sender = letter.getSender();
                    if (sender != null && sender.getId() != null && sender.getId().equals(currentUser.getId())) {
                        sentByMe = true;
                    }
                } catch (Exception e) {
                    log.warn("Error accessing sender for letter: letterId={}, error={}", 
                            letter.getId(), e.getMessage());
                }

                // 읽음 상태 확인 및 공개편지의 sentAt 조정
                Boolean isRead = null;
                LocalDateTime adjustedSentAt = getAdjustedSentAt(letter, currentUser.getId(), friendId);
                
                try {
                    // 공개편지인 경우 LetterRecipient로 읽음 상태 확인
                    if (letter.getVisibility() == Letter.Visibility.PUBLIC) {
                        LetterRecipient letterRecipient = letterRecipientRepository
                                .findByLetterIdAndUserId(letter.getId(), currentUser.getId())
                                .orElse(null);
                        if (letterRecipient != null) {
                            isRead = letterRecipient.getIsRead() != null ? letterRecipient.getIsRead() : false;
                        } else {
                            // LetterRecipient가 없으면 아직 읽지 않은 것으로 간주
                            isRead = false;
                        }
                    } else {
                        // DIRECT 편지인 경우 recipient 기준으로 읽음 상태 확인 (내가 받은 편지인 경우)
                        User recipient = letter.getRecipient();
                        if (recipient != null && recipient.getId() != null && recipient.getId().equals(currentUser.getId())) {
                            // 내가 받은 편지인 경우 읽음 상태 반환
                            isRead = letter.getIsRead() != null ? letter.getIsRead() : false;
                        }
                        // 내가 보낸 편지는 읽음 상태가 의미 없으므로 null
                    }
                } catch (Exception e) {
                    log.warn("Error accessing read status for letter: letterId={}, error={}", 
                            letter.getId(), e.getMessage());
                }

                return com.taba.friendship.dto.SharedFlowerDto.builder()
                        .id(letter.getId())
                        .letter(letterSummary)
                        .sentAt(adjustedSentAt)
                        .sentByMe(sentByMe)
                        .isRead(isRead)
                        .fontFamily(letter.getTemplateFontFamily() != null ? letter.getTemplateFontFamily() : null) // 폰트 이름 추가
                        .build();
            } catch (Exception e) {
                log.error("Error mapping letter to SharedFlowerDto: letterId={}, error={}, stackTrace={}", 
                        letter.getId(), e.getMessage(), e);
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        });
    }

    /**
     * 공개편지의 경우 조정된 sentAt을 반환합니다.
     * 공개편지에 답장이 있는 경우, 답장 시간 바로 전(1초 전)으로 조정합니다.
     */
    private LocalDateTime getAdjustedSentAt(Letter letter, String currentUserId, String friendId) {
        LocalDateTime adjustedSentAt = letter.getSentAt();
        
        try {
            // 공개편지인 경우 답장 시간을 고려하여 sentAt 조정
            if (letter.getVisibility() == Letter.Visibility.PUBLIC) {
                List<Letter> replies = letterRepository.findEarliestReplyToPublicLetter(
                        letter.getId(), currentUserId, friendId);
                if (!replies.isEmpty() && replies.get(0).getSentAt() != null) {
                    // 가장 빠른 답장 시간 바로 전 (1초 전)으로 조정
                    adjustedSentAt = replies.get(0).getSentAt().minusSeconds(1);
                }
            }
        } catch (Exception e) {
            log.warn("Error calculating adjusted sentAt for letter: letterId={}, error={}", 
                    letter.getId(), e.getMessage());
        }
        
        return adjustedSentAt;
    }

    @Transactional
    public void deleteFriend(String friendId) {
        User currentUser = SecurityUtil.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        // 양방향 친구 관계 삭제
        friendshipRepository.findByUserIds(currentUser.getId(), friendId)
                .ifPresent(friendship -> {
                    friendship.softDelete();
                    friendshipRepository.save(friendship);
                });

        friendshipRepository.findByUserIds(friendId, currentUser.getId())
                .ifPresent(friendship -> {
                    friendship.softDelete();
                    friendshipRepository.save(friendship);
                });
    }

    /**
     * 코드 없이 직접 친구 추가 (편지 답장 시 자동 친구 추가용)
     */
    @Transactional
    public void addFriendByUserIds(String userId1, String userId2) {
        // 자기 자신은 친구 추가 불가
        if (userId1.equals(userId2)) {
            return;
        }

        // 이미 친구인지 확인
        if (friendshipRepository.existsByUserIdAndFriendIdAndDeletedAtIsNull(userId1, userId2)) {
            return; // 이미 친구이면 아무것도 하지 않음
        }

        User user1 = userRepository.findActiveUserById(userId1)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        User user2 = userRepository.findActiveUserById(userId2)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 양방향 친구 관계 생성
        Friendship friendship1 = Friendship.builder()
                .user(user1)
                .friend(user2)
                .build();
        friendshipRepository.save(friendship1);

        Friendship friendship2 = Friendship.builder()
                .user(user2)
                .friend(user1)
                .build();
        friendshipRepository.save(friendship2);
    }
}

