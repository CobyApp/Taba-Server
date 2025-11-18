package com.taba.friendship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SharedFlowerDto {
    private String id;
    private LetterSummaryDto letter;
    private String flowerType;
    private LocalDateTime sentAt;
    private Boolean sentByMe;
    private Boolean isRead;
    private String fontFamily; // 폰트 이름 (편지 표시용)
}

