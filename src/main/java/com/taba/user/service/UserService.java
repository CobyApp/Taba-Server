package com.taba.user.service;

import com.taba.common.exception.BusinessException;
import com.taba.common.exception.ErrorCode;
import com.taba.friendship.repository.FriendshipRepository;
import com.taba.letter.repository.LetterRepository;
import com.taba.user.dto.UserDto;
import com.taba.user.dto.UserMapper;
import com.taba.user.entity.User;
import com.taba.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final FriendshipRepository friendshipRepository;
    private final LetterRepository letterRepository;

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
    public UserDto updateProfile(String userId, String nickname, String statusMessage, String avatarUrl) {
        User user = userRepository.findActiveUserById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.updateProfile(nickname, statusMessage, avatarUrl);
        user = userRepository.save(user);

        return userMapper.toDto(user);
    }
}

