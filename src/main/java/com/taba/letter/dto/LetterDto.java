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
    private Letter.Visibility visibility;
    private Boolean isAnonymous;
    private LocalDateTime sentAt;
    private Integer views;
    private List<String> attachedImages;
    private LetterTemplateDto template;
}


