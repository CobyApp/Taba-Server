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
}

