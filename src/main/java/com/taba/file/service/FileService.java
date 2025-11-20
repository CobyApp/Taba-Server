package com.taba.file.service;

import com.taba.common.exception.BusinessException;
import com.taba.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class FileService {

    @Value("${file.upload.dir:uploads}")
    private String uploadDir;

    @Value("${file.upload.max-size:10485760}")
    private long maxFileSize; // 10MB 기본값

    @Value("${file.upload.allowed-types:image/jpeg,image/png,image/gif,image/webp}")
    private String allowedTypes;

    @Getter
    @AllArgsConstructor
    public static class FileUploadResult {
        private String fileId;
        private String url;
    }

    public FileUploadResult uploadImage(MultipartFile file) {
        try {
            // 파일 존재 여부 확인
            if (file == null || file.isEmpty()) {
                throw new BusinessException(ErrorCode.INVALID_REQUEST, "파일이 없습니다.");
            }

            // 파일명 확인
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                throw new BusinessException(ErrorCode.INVALID_REQUEST, "파일명이 없습니다.");
            }

            // 파일 크기 검증
            if (file.getSize() > maxFileSize) {
                throw new BusinessException(ErrorCode.INVALID_REQUEST, 
                    String.format("파일 크기가 너무 큽니다. 최대 크기: %d bytes", maxFileSize));
            }

            // 파일 타입 검증
            String contentType = file.getContentType();
            if (contentType == null || !isAllowedFileType(contentType)) {
                List<String> allowedTypesList = Arrays.asList(allowedTypes.split(","));
                throw new BusinessException(ErrorCode.INVALID_REQUEST, 
                    String.format("허용되지 않은 파일 타입입니다. 허용된 타입: %s", allowedTypesList));
            }

            // 파일 확장자 추출
            String extension = "";
            int lastDotIndex = originalFilename.lastIndexOf('.');
            if (lastDotIndex > 0) {
                extension = originalFilename.substring(lastDotIndex);
            }

            // UUID 기반 고유 파일명 생성
            String fileName = UUID.randomUUID().toString() + extension;
            String fileId = fileName; // fileId는 전체 파일명 (UUID + 확장자)
            Path uploadPath = Paths.get(uploadDir);

            // 업로드 디렉토리 생성
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 파일 저장
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            log.info("File uploaded successfully: {} (size: {} bytes, type: {})", fileName, file.getSize(), contentType);

            // 실제 URL 반환 (서버 주소 + 경로)
            String baseUrl = System.getenv("SERVER_URL");
            if (baseUrl == null || baseUrl.isEmpty()) {
                baseUrl = "http://localhost:8080/api/v1";
            }
            String url = baseUrl + "/uploads/" + fileName;

            return new FileUploadResult(fileId, url);
        } catch (BusinessException e) {
            throw e;
        } catch (IOException e) {
            log.error("File upload failed", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
        }
    }

    private boolean isAllowedFileType(String contentType) {
        List<String> allowedTypesList = Arrays.asList(allowedTypes.split(","));
        return allowedTypesList.stream()
                .anyMatch(allowed -> contentType.toLowerCase().startsWith(allowed.trim().toLowerCase()));
    }
}

