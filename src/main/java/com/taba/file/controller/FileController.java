package com.taba.file.controller;

import com.taba.common.dto.ApiResponse;
import com.taba.file.service.FileService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping
    public ResponseEntity<ApiResponse<FileUploadResponse>> uploadFile(@RequestParam("file") MultipartFile file) {
        FileService.FileUploadResult result = fileService.uploadImage(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                new FileUploadResponse(result.getFileId(), result.getUrl())));
    }

    @Getter
    @AllArgsConstructor
    public static class FileUploadResponse {
        private String fileId;
        private String url;
    }
}

