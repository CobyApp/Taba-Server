package com.taba.friendship.service;

import com.taba.common.exception.BusinessException;
import com.taba.common.exception.ErrorCode;
import com.taba.common.util.SecurityUtil;
import com.taba.friendship.dto.BouquetDto;
import com.taba.friendship.entity.Friendship;
import com.taba.friendship.entity.SharedFlower;
import com.taba.friendship.repository.FriendshipRepository;
import com.taba.friendship.repository.SharedFlowerRepository;
import com.taba.invite.entity.InviteCode;
import com.taba.invite.repository.InviteCodeRepository;
import com.taba.user.entity.User;
import com.taba.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final SharedFlowerRepository sharedFlowerRepository;
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
                    long unreadCount = sharedFlowerRepository.countUnreadByFriendshipId(friendship.getId());
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

        Friendship friendship = friendshipRepository.findByUserIds(currentUser.getId(), friendId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FRIENDSHIP_NOT_FOUND));

        org.springframework.data.domain.Page<SharedFlower> sharedFlowers = 
                sharedFlowerRepository.findByFriendshipId(friendship.getId(), pageable);

        return sharedFlowers.map(sf -> {
            com.taba.friendship.dto.LetterSummaryDto letterSummary = 
                    com.taba.friendship.dto.LetterSummaryDto.builder()
                            .id(sf.getLetter().getId())
                            .title(sf.getLetter().getTitle())
                            .preview(sf.getLetter().getPreview())
                            .build();

            return com.taba.friendship.dto.SharedFlowerDto.builder()
                    .id(sf.getId())
                    .letter(letterSummary)
                    .flowerType(sf.getLetter().getFlowerType().name())
                    .sentAt(sf.getSentAt())
                    .sentByMe(sf.getSentByUser().getId().equals(currentUser.getId()))
                    .isRead(sf.getIsRead())
                    .build();
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
}

