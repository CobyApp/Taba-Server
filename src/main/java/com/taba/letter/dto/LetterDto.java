package com.taba.letter.dto;

import com.taba.letter.entity.Letter;
import com.taba.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LetterDto {
    private String id;
    private String title;
    private String content;
    private String preview;
    private UserDto sender;
    private Letter.FlowerType flowerType;
    private Letter.Visibility visibility;
    private Boolean isAnonymous;
    private LocalDateTime sentAt;
    private Integer likes;
    private Integer views;
    private Integer savedCount;
    private Boolean isLiked;
    private Boolean isSaved;
    private List<String> attachedImages;
    private LetterTemplateDto template;
    private PositionDto position;
}


