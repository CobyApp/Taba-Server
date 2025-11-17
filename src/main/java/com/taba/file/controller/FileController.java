package com.taba.file.controller;

import com.taba.common.dto.ApiResponse;
import com.taba.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<FileUploadResponse>> uploadFile(@RequestParam("file") MultipartFile file) {
        String url = fileService.uploadImage(file);
        return ResponseEntity.ok(ApiResponse.success(
                new FileUploadResponse(url, file.getOriginalFilename())));
    }

    @lombok.Getter
    @lombok.AllArgsConstructor
    static class FileUploadResponse {
        private String url;
        private String fileName;
    }
}

