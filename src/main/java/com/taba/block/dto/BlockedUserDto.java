package com.taba.block.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 차단한 사용자 정보 DTO
 */
@Getter
@Builder
public class BlockedUserDto {
    private String id;           // 사용자 ID
    private String nickname;     // 닉네임
    private String avatarUrl;    // 프로필 이미지 URL
    private LocalDateTime blockedAt; // 차단한 시간
}

