package com.taba.letter.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LetterTemplateRequest {
    private String background;
    private String textColor;
    private String fontFamily;
    private Double fontSize;
}

