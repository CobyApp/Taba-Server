package com.taba.letter.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.taba.letter.entity.Letter;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true) // 알 수 없는 필드 무시 (flowerType 등)
public class LetterCreateRequest {
    @NotBlank(message = "{validation.title.required}")
    private String title;

    @NotBlank(message = "{validation.content.required}")
    private String content;

    @NotBlank(message = "{validation.preview.required}")
    private String preview;

    // 답장 API에서는 선택사항 (서버에서 자동으로 DIRECT로 설정)
    // 일반 편지 작성 시에는 서비스 레이어에서 null 체크
    private Letter.Visibility visibility;

    private com.taba.letter.dto.LetterTemplateRequest template;

    private List<String> attachedImages;

    private LocalDateTime scheduledAt;

    private String recipientId;

    private String language; // ko, en, ja
}


