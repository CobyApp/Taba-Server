package com.taba.user.service;

import com.taba.common.exception.BusinessException;
import com.taba.common.exception.ErrorCode;
import com.taba.file.service.FileService;
import com.taba.friendship.repository.FriendshipRepository;
import com.taba.letter.repository.LetterRepository;
import com.taba.user.dto.UserDto;
import com.taba.user.dto.UserMapper;
import com.taba.user.entity.User;
import com.taba.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final FriendshipRepository friendshipRepository;
    private final LetterRepository letterRepository;
    private final FileService fileService;
    
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public UserDto getProfile(String userId) {
        User user = userRepository.findActiveUserById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        UserDto userDto = userMapper.toDto(user);
        
        // friendCount 계산
        long friendCount = friendshipRepository.findAllByUserId(userId).size();
        userDto.setFriendCount((int) friendCount);
        
        // sentLetters 계산
        long sentLetters = letterRepository.findBySenderId(userId, 
                org.springframework.data.domain.Pageable.unpaged()).getTotalElements();
        userDto.setSentLetters((int) sentLetters);
        
        return userDto;
    }

    /**
     * User 엔티티를 최신 데이터로 새로고침하여 반환합니다.
     * 닉네임이나 프로필 변경사항이 즉시 반영됩니다.
     * 
     * JPA 1차 캐시에서 엔티티를 detach하고 DB에서 새로 로드하여
     * 항상 최신 데이터를 반환합니다.
     */
    @Transactional(readOnly = true)
    public User refreshUser(User user) {
        if (user == null || user.getId() == null) {
            return user;
        }
        
        // 1차 캐시에서 기존 엔티티 제거 (캐시된 오래된 데이터 방지)
        if (entityManager.contains(user)) {
            entityManager.detach(user);
        }
        
        return userRepository.findActiveUserById(user.getId())
                .orElse(user);
    }

    /**
     * User ID로 최신 User 엔티티를 조회합니다.
     */
    @Transactional(readOnly = true)
    public User getFreshUser(String userId) {
        if (userId == null) {
            return null;
        }
        return userRepository.findActiveUserById(userId).orElse(null);
    }

    @Transactional
    public UserDto updateProfile(String userId, String nickname, String avatarUrl, MultipartFile profileImage) {
        User user = userRepository.findActiveUserById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 프로필 이미지 업로드 처리
        String finalAvatarUrl = avatarUrl;
        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                finalAvatarUrl = fileService.uploadImage(profileImage).getUrl();
            } catch (Exception e) {
                log.error("프로필 이미지 업로드 실패", e);
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }

        user.updateProfile(nickname, finalAvatarUrl);
        user = userRepository.save(user);

        return userMapper.toDto(user);
    }

    @Transactional
    public void updateFcmToken(String userId, String fcmToken) {
        User user = userRepository.findActiveUserById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        user.updateFcmToken(fcmToken);
        userRepository.save(user);
        log.info("FCM token updated for user: {}", userId);
    }

    @Transactional
    public void deleteFcmToken(String userId) {
        User user = userRepository.findActiveUserById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        user.updateFcmToken(null);
        userRepository.save(user);
        log.info("FCM token deleted for user: {}", userId);
    }

    @Transactional
    public void withdrawUser(String userId) {
        User user = userRepository.findActiveUserById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 사용자 소프트 삭제
        user.softDelete();
        userRepository.save(user);
        
        log.info("User withdrawn: {}", userId);
    }
}

