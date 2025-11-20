package com.taba.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String id;
    private String email;
    private String nickname;
    private String avatarUrl;
    private LocalDateTime joinedAt;
    private Integer friendCount;
    private Integer sentLetters;
    private Integer unreadLetterCount; // 안 읽은 편지 개수 (친구 목록 조회 시 사용)
}

