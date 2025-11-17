package com.taba.friendship.dto;

import com.taba.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BouquetDto {
    private UserDto friend;
    private List<SharedFlowerDto> sharedFlowers;
    private BigDecimal bloomLevel;
    private Integer trustScore;
    private String bouquetName;
    private Integer unreadCount;
}


