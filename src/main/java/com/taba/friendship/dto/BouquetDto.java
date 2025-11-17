package com.taba.friendship.dto;

import com.taba.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BouquetDto {
    private UserDto friend;
    private BigDecimal bloomLevel;
    private Integer trustScore;
    private String bouquetName;
    private Integer unreadCount; // 읽지 않은 편지 수
}


