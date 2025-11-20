package com.taba.auth.service;

import com.taba.auth.dto.AuthResponse;
import com.taba.auth.dto.LoginRequest;
import com.taba.auth.dto.SignupRequest;
import com.taba.auth.entity.PasswordResetToken;
import com.taba.auth.repository.PasswordResetTokenRepository;
import com.taba.auth.util.JwtTokenProvider;
import com.taba.common.exception.BusinessException;
import com.taba.common.exception.ErrorCode;
import com.taba.common.service.EmailService;
import com.taba.file.service.FileService;
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
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.util.Date;

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
    private final FileService fileService;
    private final EmailService emailService;
    
    private static final String UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER_CASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*";
    private static final String ALL_CHARS = UPPER_CASE + LOWER_CASE + DIGITS + SPECIAL_CHARS;
    private static final SecureRandom random = new SecureRandom();

    @Transactional
    public AuthResponse signup(SignupRequest request, MultipartFile profileImage) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 프로필 이미지 업로드 처리
        String avatarUrl = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                avatarUrl = fileService.uploadImage(profileImage);
            } catch (Exception e) {
                log.error("프로필 이미지 업로드 실패", e);
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .nickname(request.getNickname())
                .avatarUrl(avatarUrl)
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

        // 임시 비밀번호 생성 (12자리: 대문자, 소문자, 숫자, 특수문자 포함)
        String temporaryPassword = generateTemporaryPassword(12);
        
        // 사용자 비밀번호를 임시 비밀번호로 업데이트
        user.updatePassword(passwordEncoder.encode(temporaryPassword));
        userRepository.save(user);

        // 이메일로 임시 비밀번호 발송
        try {
            emailService.sendTemporaryPassword(user.getEmail(), user.getNickname(), temporaryPassword);
            log.info("Temporary password sent to email: {}", email);
        } catch (Exception e) {
            log.error("Failed to send temporary password email to: {}", email, e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "이메일 발송에 실패했습니다.");
        }
    }

    /**
     * 임시 비밀번호 생성
     * @param length 비밀번호 길이
     * @return 생성된 임시 비밀번호
     */
    private String generateTemporaryPassword(int length) {
        StringBuilder password = new StringBuilder(length);
        
        // 최소 1개씩 각 종류의 문자 포함 보장
        password.append(UPPER_CASE.charAt(random.nextInt(UPPER_CASE.length())));
        password.append(LOWER_CASE.charAt(random.nextInt(LOWER_CASE.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.append(SPECIAL_CHARS.charAt(random.nextInt(SPECIAL_CHARS.length())));
        
        // 나머지 문자 랜덤 생성
        for (int i = password.length(); i < length; i++) {
            password.append(ALL_CHARS.charAt(random.nextInt(ALL_CHARS.length())));
        }
        
        // 문자 순서 섞기
        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }
        
        return new String(passwordArray);
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
}

