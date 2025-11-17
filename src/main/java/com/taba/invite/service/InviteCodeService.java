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

    private final InviteCodeRepository inviteCodeRepository;

    @Transactional
    public InviteCodeDto generateInviteCode() {
        User currentUser = SecurityUtil.getCurrentUser();
        if (currentUser == null) {
            throw new com.taba.common.exception.BusinessException(com.taba.common.exception.ErrorCode.UNAUTHORIZED);
        }

        // 기존 활성 코드가 있으면 반환
        InviteCode existingCode = inviteCodeRepository.findActiveByUserId(currentUser.getId()).orElse(null);
        if (existingCode != null && !existingCode.isExpired()) {
            return toDto(existingCode);
        }

        // 새 코드 생성
        String code = currentUser.getUsername() + "-" + generateRandomDigits();
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

        InviteCode inviteCode = inviteCodeRepository.findActiveByUserId(currentUser.getId())
                .orElse(null);

        if (inviteCode == null || inviteCode.isExpired()) {
            return null;
        }

        return toDto(inviteCode);
    }

    private String generateRandomDigits() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    private InviteCodeDto toDto(InviteCode inviteCode) {
        return InviteCodeDto.builder()
                .code(inviteCode.getCode())
                .expiresAt(inviteCode.getExpiresAt())
                .remainingMinutes(inviteCode.getRemainingMinutes())
                .build();
    }
}

