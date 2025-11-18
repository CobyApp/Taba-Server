package com.taba.letter.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.taba.letter.entity.Letter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true) // 알 수 없는 필드 무시 (flowerType 등)
public class LetterCreateRequest {
    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    @NotBlank(message = "미리보기는 필수입니다.")
    private String preview;

    @NotNull(message = "공개 설정은 필수입니다.")
    private Letter.Visibility visibility;

    private Boolean isAnonymous = false;

    private com.taba.letter.dto.LetterTemplateRequest template;

    private List<String> attachedImages;

    private LocalDateTime scheduledAt;

    private String recipientId;
}


