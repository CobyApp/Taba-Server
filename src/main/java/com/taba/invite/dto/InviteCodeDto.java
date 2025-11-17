package com.taba.invite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InviteCodeDto {
    private String code;
    private LocalDateTime expiresAt;
    private Long remainingMinutes;
}

