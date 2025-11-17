package com.taba.letter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LetterTemplateDto {
    private String background;
    private String textColor;
    private String fontFamily;
    private Double fontSize;
}

