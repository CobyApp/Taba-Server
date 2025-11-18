package com.taba.invite.service;

import com.taba.common.util.SecurityUtil;
import com.taba.invite.dto.InviteCodeDto;
import com.taba.invite.entity.InviteCode;
import com.taba.invite.repository.InviteCodeRepository;
import com.taba.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class InviteCodeService {

    private static final int INVITE_CODE_LENGTH = 6; // 초대 코드는 6자리로 고정
    private static final String INVITE_CODE_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private final InviteCodeRepository inviteCodeRepository;

    @Transactional
    public InviteCodeDto generateInviteCode() {
        User currentUser = SecurityUtil.getCurrentUser();
        if (currentUser == null) {
            throw new com.taba.common.exception.BusinessException(com.taba.common.exception.ErrorCode.UNAUTHORIZED);
        }

        // 기존 활성 코드가 있으면 반환
        InviteCode existingCode = inviteCodeRepository.findFirstActiveByUserId(currentUser.getId()).orElse(null);
        if (existingCode != null && !existingCode.isExpired()) {
            return toDto(existingCode);
        }

        // 새 코드 생성 (6자리 숫자+영문 조합)
        String code;
        do {
            code = generateRandomAlphanumeric(INVITE_CODE_LENGTH);
        } while (inviteCodeRepository.findByCode(code).isPresent()); // 중복 체크
        
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(3);

        InviteCode inviteCode = InviteCode.builder()
                .user(currentUser)
                .code(code)
                .expiresAt(expiresAt)
                .build();

        inviteCode = inviteCodeRepository.save(inviteCode);
        return toDto(inviteCode);
    }

    @Transactional(readOnly = true)
    public InviteCodeDto getCurrentInviteCode() {
        User currentUser = SecurityUtil.getCurrentUser();
        if (currentUser == null) {
            throw new com.taba.common.exception.BusinessException(com.taba.common.exception.ErrorCode.UNAUTHORIZED);
        }

        InviteCode inviteCode = inviteCodeRepository.findFirstActiveByUserId(currentUser.getId())
                .orElse(null);

        if (inviteCode == null || inviteCode.isExpired()) {
            return null;
        }

        return toDto(inviteCode);
    }

    /**
     * 6자리 숫자+영문 조합 코드 생성
     * 대문자 영문과 숫자만 사용 (0-9, A-Z)
     * 항상 정확히 6자리로 생성됨
     */
    private String generateRandomAlphanumeric(int length) {
        if (length != INVITE_CODE_LENGTH) {
            throw new IllegalArgumentException("초대 코드는 " + INVITE_CODE_LENGTH + "자리여야 합니다.");
        }
        Random random = new Random();
        StringBuilder sb = new StringBuilder(INVITE_CODE_LENGTH);
        for (int i = 0; i < INVITE_CODE_LENGTH; i++) {
            sb.append(INVITE_CODE_CHARS.charAt(random.nextInt(INVITE_CODE_CHARS.length())));
        }
        return sb.toString();
    }

    private InviteCodeDto toDto(InviteCode inviteCode) {
        return InviteCodeDto.builder()
                .code(inviteCode.getCode())
                .expiresAt(inviteCode.getExpiresAt())
                .remainingMinutes(inviteCode.getRemainingMinutes())
                .build();
    }
}

