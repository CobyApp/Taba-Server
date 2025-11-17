package com.taba.letter.controller;

import com.taba.common.dto.ApiResponse;
import com.taba.letter.dto.LetterCreateRequest;
import com.taba.letter.dto.LetterDto;
import com.taba.letter.service.LetterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @PageableDefault(size = 20) Pageable pageable) {
        Page<LetterDto> letters = letterService.getPublicLetters(pageable);
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
        return ResponseEntity.ok(ApiResponse.success("신고가 접수되었습니다."));
    }

    @PostMapping("/{letterId}/reply")
    public ResponseEntity<ApiResponse<LetterDto>> replyLetter(
            @PathVariable String letterId,
            @Valid @RequestBody LetterCreateRequest request) {
        LetterDto replyLetter = letterService.replyLetter(letterId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(replyLetter, "답장이 전송되었습니다. 친구가 자동으로 추가되었습니다."));
    }

    @DeleteMapping("/{letterId}")
    public ResponseEntity<ApiResponse<?>> deleteLetter(@PathVariable String letterId) {
        letterService.deleteLetter(letterId);
        return ResponseEntity.ok(ApiResponse.success("편지가 삭제되었습니다."));
    }

    @lombok.Getter
    @lombok.Setter
    static class ReportRequest {
        private String reason;
    }
}

