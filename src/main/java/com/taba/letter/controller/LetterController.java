package com.taba.letter.controller;

import com.taba.common.dto.ApiResponse;
import com.taba.common.util.MessageUtil;
import com.taba.common.util.SecurityUtil;
import com.taba.letter.dto.LetterCreateRequest;
import com.taba.letter.dto.LetterDto;
import com.taba.letter.service.LetterService;
import com.taba.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/letters")
@RequiredArgsConstructor
public class LetterController {

    private final LetterService letterService;

    @PostMapping
    public ResponseEntity<ApiResponse<LetterDto>> createLetter(@Valid @RequestBody LetterCreateRequest request) {
        LetterDto letterDto = letterService.createLetter(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(letterDto));
    }

    @GetMapping("/public")
    public ResponseEntity<ApiResponse<Page<LetterDto>>> getPublicLetters(
            @RequestParam(required = false) List<String> languages,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<LetterDto> letters = letterService.getPublicLetters(pageable, languages);
        return ResponseEntity.ok(ApiResponse.success(letters));
    }

    @GetMapping("/{letterId}")
    public ResponseEntity<ApiResponse<LetterDto>> getLetter(@PathVariable String letterId) {
        LetterDto letterDto = letterService.getLetter(letterId);
        return ResponseEntity.ok(ApiResponse.success(letterDto));
    }

    @PostMapping("/{letterId}/report")
    public ResponseEntity<ApiResponse<?>> reportLetter(
            @PathVariable String letterId,
            @RequestBody ReportRequest request) {
        letterService.reportLetter(letterId, request.getReason());
        User currentUser = SecurityUtil.getCurrentUser();
        String language = currentUser != null && currentUser.getLanguage() != null ? currentUser.getLanguage() : "ko";
        String message = MessageUtil.getMessage("api.letter.reported", language);
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @PostMapping("/{letterId}/reply")
    public ResponseEntity<ApiResponse<LetterDto>> replyLetter(
            @PathVariable String letterId,
            @Valid @RequestBody LetterCreateRequest request) {
        LetterDto replyLetter = letterService.replyLetter(letterId, request);
        User currentUser = SecurityUtil.getCurrentUser();
        String language = currentUser != null && currentUser.getLanguage() != null ? currentUser.getLanguage() : "ko";
        String message = MessageUtil.getMessage("api.letter.reply_sent", language);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(replyLetter, message));
    }

    @DeleteMapping("/{letterId}")
    public ResponseEntity<ApiResponse<?>> deleteLetter(@PathVariable String letterId) {
        letterService.deleteLetter(letterId);
        User currentUser = SecurityUtil.getCurrentUser();
        String language = currentUser != null && currentUser.getLanguage() != null ? currentUser.getLanguage() : "ko";
        String message = MessageUtil.getMessage("api.letter.deleted", language);
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @lombok.Getter
    @lombok.Setter
    static class ReportRequest {
        private String reason;
    }
}

