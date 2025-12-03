package com.taba.block.service;

import com.taba.block.dto.BlockedUserDto;
import com.taba.block.entity.Block;
import com.taba.block.repository.BlockRepository;
import com.taba.common.exception.BusinessException;
import com.taba.common.exception.ErrorCode;
import com.taba.common.util.SecurityUtil;
import com.taba.friendship.entity.Friendship;
import com.taba.friendship.repository.FriendshipRepository;
import com.taba.user.entity.User;
import com.taba.user.repository.UserRepository;
import com.taba.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class BlockService {

    private final BlockRepository blockRepository;
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final UserService userService;

    /**
     * 사용자 차단
     * - 차단 관계 생성
     * - 친구 관계가 있으면 삭제 (양방향)
     */
    @Transactional
    public void blockUser(String blockedUserId) {
        String currentUserId = SecurityUtil.getCurrentUserId();
        if (currentUserId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        // 자기 자신은 차단 불가
        if (currentUserId.equals(blockedUserId)) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        // 현재 사용자 조회 (영속 상태)
        User currentUser = userRepository.findActiveUserById(currentUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED));

        // 차단할 사용자 조회
        User blockedUser = userRepository.findActiveUserById(blockedUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 이미 차단한 경우 확인
        if (blockRepository.existsByBlockerIdAndBlockedId(currentUserId, blockedUserId)) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        // 차단 관계 생성
        Block block = Block.builder()
                .blocker(currentUser)
                .blocked(blockedUser)
                .build();
        blockRepository.save(block);

        // 친구 관계가 있으면 삭제 (양방향)
        List<Friendship> friendships = friendshipRepository.findByUserIdsList(currentUserId, blockedUserId);
        for (Friendship friendship : friendships) {
            if (!friendship.isDeleted()) {
                friendship.softDelete();
                friendshipRepository.save(friendship);
            }
        }

        log.info("User {} blocked user {}", currentUserId, blockedUserId);
    }

    /**
     * 차단 해제
     */
    @Transactional
    public void unblockUser(String blockedUserId) {
        String currentUserId = SecurityUtil.getCurrentUserId();
        if (currentUserId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        // 차단 관계 조회
        Block block = blockRepository.findByBlockerIdAndBlockedId(currentUserId, blockedUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_REQUEST));

        // 차단 해제 (soft delete)
        block.softDelete();
        blockRepository.save(block);

        log.info("User {} unblocked user {}", currentUserId, blockedUserId);
    }

    /**
     * 차단한 사용자 목록 조회
     */
    @Transactional(readOnly = true)
    public List<BlockedUserDto> getBlockedUsers() {
        String currentUserId = SecurityUtil.getCurrentUserId();
        if (currentUserId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        List<Block> blocks = blockRepository.findByBlockerId(currentUserId);
        
        return blocks.stream()
                .map(block -> {
                    // blocked 사용자 정보를 최신 데이터로 조회
                    User blockedUser = userService.refreshUser(block.getBlocked());
                    return BlockedUserDto.builder()
                            .id(blockedUser.getId())
                            .nickname(blockedUser.getNickname())
                            .avatarUrl(blockedUser.getAvatarUrl())
                            .blockedAt(block.getCreatedAt())
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 특정 사용자가 차단한 사용자 ID 목록 조회
     */
    @Transactional(readOnly = true)
    public List<String> getBlockedUserIds(String userId) {
        return blockRepository.findBlockedUserIdsByBlockerId(userId);
    }

    /**
     * 차단 여부 확인 (A가 B를 차단했는지)
     */
    @Transactional(readOnly = true)
    public boolean isBlocked(String blockerId, String blockedId) {
        return blockRepository.existsByBlockerIdAndBlockedId(blockerId, blockedId);
    }

    /**
     * 양방향 차단 여부 확인 (A가 B를 차단했거나, B가 A를 차단한 경우)
     */
    @Transactional(readOnly = true)
    public boolean isBlockedBetween(String userId1, String userId2) {
        return blockRepository.existsBlockBetweenUsers(userId1, userId2);
    }
}

