package com.taba.friendship.service;

import com.taba.common.exception.BusinessException;
import com.taba.common.exception.ErrorCode;
import com.taba.common.util.SecurityUtil;
import com.taba.friendship.dto.BouquetDto;
import com.taba.friendship.entity.Friendship;
import com.taba.friendship.repository.FriendshipRepository;
import com.taba.friendship.repository.SharedFlowerRepository;
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

    @Transactional
    public Friendship addFriend(String inviteCode) {
        User currentUser = SecurityUtil.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        // TODO: InviteCode 검증 및 사용 처리
        // 임시로 직접 친구 추가 로직 구현 필요

        return null;
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

    @Transactional
    public void deleteFriend(String friendId) {
        User currentUser = SecurityUtil.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        Friendship friendship = friendshipRepository.findByUserIds(currentUser.getId(), friendId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FRIENDSHIP_NOT_FOUND));

        friendship.softDelete();
        friendshipRepository.save(friendship);
    }
}

