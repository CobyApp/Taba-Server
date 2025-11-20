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

