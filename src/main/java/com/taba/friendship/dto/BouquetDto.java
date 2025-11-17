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

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public static class SharedFlowerDto {
    private String id;
    private LetterSummaryDto letter;
    private String flowerType;
    private java.time.LocalDateTime sentAt;
    private Boolean sentByMe;
    private Boolean isRead;
}

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public static class LetterSummaryDto {
    private String id;
    private String title;
    private String preview;
}

