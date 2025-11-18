package com.taba.friendship.service;

import com.taba.common.exception.BusinessException;
import com.taba.common.exception.ErrorCode;
import com.taba.common.util.SecurityUtil;
import com.taba.friendship.dto.BouquetDto;
import com.taba.friendship.entity.Friendship;
import com.taba.friendship.repository.FriendshipRepository;
import com.taba.letter.entity.Letter;
import com.taba.letter.repository.LetterRepository;
import com.taba.invite.entity.InviteCode;
import com.taba.invite.repository.InviteCodeRepository;
import com.taba.user.entity.User;
import com.taba.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final LetterRepository letterRepository;
    private final UserRepository userRepository;
    private final InviteCodeRepository inviteCodeRepository;

    @Transactional
    public Friendship addFriend(String inviteCode) {
        User currentUser = SecurityUtil.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        // InviteCode 검증
        InviteCode code = inviteCodeRepository.findByCode(inviteCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_INVITE_CODE));

        if (code.isExpired()) {
            throw new BusinessException(ErrorCode.INVITE_CODE_EXPIRED);
        }

        if (code.isUsed()) {
            throw new BusinessException(ErrorCode.INVITE_CODE_ALREADY_USED);
        }

        // 자기 자신의 초대 코드는 사용 불가
        if (code.getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException(ErrorCode.CANNOT_USE_OWN_INVITE_CODE);
        }

        // 이미 친구인지 확인
        if (friendshipRepository.existsByUserIdAndFriendIdAndDeletedAtIsNull(
                currentUser.getId(), code.getUser().getId())) {
            throw new BusinessException(ErrorCode.ALREADY_FRIENDS);
        }

        // 양방향 친구 관계 생성
        Friendship friendship1 = Friendship.builder()
                .user(currentUser)
                .friend(code.getUser())
                .build();
        friendshipRepository.save(friendship1);

        Friendship friendship2 = Friendship.builder()
                .user(code.getUser())
                .friend(currentUser)
                .build();
        friendshipRepository.save(friendship2);

        // 초대 코드 사용 처리
        code.use(currentUser);
        inviteCodeRepository.save(code);

        return friendship1;
    }

    @Transactional(readOnly = true)
    public List<BouquetDto> getBouquets() {
        User currentUser = SecurityUtil.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        List<Friendship> friendships = friendshipRepository.findByUserId(currentUser.getId());
        return friendships.stream()
                .map(friendship -> {
                    long unreadCount = letterRepository.countUnreadLettersBetweenFriends(
                            currentUser.getId(), friendship.getFriend().getId());
                    return BouquetDto.builder()
                            .friend(com.taba.user.dto.UserMapper.INSTANCE.toDto(friendship.getFriend()))
                            .bloomLevel(friendship.getBloomLevel())
                            .trustScore(friendship.getTrustScore())
                            .bouquetName(friendship.getBouquetName())
                            .unreadCount((int) unreadCount)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateBouquetName(String friendId, String bouquetName) {
        User currentUser = SecurityUtil.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        Friendship friendship = friendshipRepository.findByUserIds(currentUser.getId(), friendId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FRIENDSHIP_NOT_FOUND));

        friendship.updateBouquetName(bouquetName);
        friendshipRepository.save(friendship);
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

        // 친구 간 주고받은 편지 조회
        org.springframework.data.domain.Page<Letter> letters = 
                letterRepository.findLettersBetweenFriends(currentUser.getId(), friendId, pageable);

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

                // recipient 기준으로 읽음 상태 확인 (내가 받은 편지인 경우)
                Boolean isRead = null;
                if (letter.getRecipient() != null) {
                    String recipientId = letter.getRecipient().getId();
                    if (recipientId != null && recipientId.equals(currentUser.getId())) {
                        // 내가 받은 편지인 경우 읽음 상태 반환
                        isRead = letter.getIsRead() != null ? letter.getIsRead() : false;
                    }
                }
                // 내가 보낸 편지는 읽음 상태가 의미 없으므로 null

                // sentByMe 확인
                boolean sentByMe = false;
                if (letter.getSender() != null) {
                    String senderId = letter.getSender().getId();
                    if (senderId != null && senderId.equals(currentUser.getId())) {
                        sentByMe = true;
                    }
                }

                return com.taba.friendship.dto.SharedFlowerDto.builder()
                        .id(letter.getId())
                        .letter(letterSummary)
                        .flowerType(letter.getFlowerType() != null ? letter.getFlowerType().name() : "ROSE")
                        .sentAt(letter.getSentAt())
                        .sentByMe(sentByMe)
                        .isRead(isRead)
                        .fontFamily(letter.getTemplateFontFamily() != null ? letter.getTemplateFontFamily() : null) // 폰트 이름 추가
                        .build();
            } catch (Exception e) {
                log.error("Error mapping letter to SharedFlowerDto: letterId={}, error={}", 
                        letter.getId(), e.getMessage(), e);
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        });
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

