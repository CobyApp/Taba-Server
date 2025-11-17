package com.taba.auth.service;

import com.taba.auth.dto.AuthResponse;
import com.taba.auth.dto.LoginRequest;
import com.taba.auth.dto.SignupRequest;
import com.taba.auth.entity.PasswordResetToken;
import com.taba.auth.repository.PasswordResetTokenRepository;
import com.taba.auth.util.JwtTokenProvider;
import com.taba.common.exception.BusinessException;
import com.taba.common.exception.ErrorCode;
import com.taba.user.dto.UserDto;
import com.taba.user.dto.UserMapper;
import com.taba.user.entity.User;
import com.taba.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserMapper userMapper;
    private final TokenBlacklistService tokenBlacklistService;

    @Transactional
    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        String username = generateUniqueUsername();
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .username(username)
                .nickname(request.getNickname())
                .build();

        user = userRepository.save(user);
        String token = jwtTokenProvider.generateToken(user.getId());
        UserDto userDto = userMapper.toDto(user);

        return AuthResponse.builder()
                .token(token)
                .user(userDto)
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findActiveUserByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        String token = jwtTokenProvider.generateToken(user.getId());
        UserDto userDto = userMapper.toDto(user);

        return AuthResponse.builder()
                .token(token)
                .user(userDto)
                .build();
    }

    @Transactional
    public void forgotPassword(String email) {
        User user = userRepository.findActiveUserByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .user(user)
                .token(token)
                .expiresAt(expiresAt)
                .build();

        passwordResetTokenRepository.save(resetToken);
        // TODO: 실제 이메일 발송 로직 구현 (Spring Mail 사용)
        log.info("Password reset token generated for user: {} - Token: {}", email, token);
        log.info("Password reset link: http://localhost:3000/reset-password?token={}", token);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));

        if (resetToken.isExpired()) {
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED);
        }

        if (resetToken.isUsed()) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        User user = resetToken.getUser();
        user.updatePassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.markAsUsed();
        passwordResetTokenRepository.save(resetToken);
    }

    @Transactional
    public void changePassword(String userId, String currentPassword, String newPassword) {
        User user = userRepository.findActiveUserById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        user.updatePassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public void logout(String token) {
        try {
            // 토큰에서 만료 시간 추출
            Claims claims = jwtTokenProvider.getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            long expirationTime = expiration.getTime();
            
            // 블랙리스트에 추가 (Redis가 없어도 에러 발생 안 함)
            tokenBlacklistService.addToBlacklist(token, expirationTime);
            log.info("Token added to blacklist (if Redis is available)");
        } catch (Exception e) {
            log.debug("Failed to add token to blacklist (Redis may be unavailable): {}", e.getMessage());
        }
    }

    private String generateUniqueUsername() {
        String username;
        do {
            username = "user" + System.currentTimeMillis();
        } while (userRepository.existsByUsername(username));
        return username;
    }
}

