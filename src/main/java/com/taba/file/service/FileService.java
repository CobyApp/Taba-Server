package com.taba.file.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
public class FileService {

    @Value("${file.upload.dir:uploads}")
    private String uploadDir;

    public String uploadImage(MultipartFile file) {
        try {
            // 파일명에서 특수문자 제거 및 안전한 파일명 생성
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                throw new RuntimeException("파일명이 없습니다.");
            }
            
            // 파일 확장자 추출
            String extension = "";
            int lastDotIndex = originalFilename.lastIndexOf('.');
            if (lastDotIndex > 0) {
                extension = originalFilename.substring(lastDotIndex);
            }
            
            // UUID 기반 고유 파일명 생성
            String fileName = UUID.randomUUID().toString() + extension;
            Path uploadPath = Paths.get(uploadDir);
            
            // 업로드 디렉토리 생성
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // 파일 저장
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);
            
            log.info("File uploaded successfully: {}", fileName);
            
            // 실제 URL 반환 (서버 주소 + 경로)
            String baseUrl = System.getenv("SERVER_URL");
            if (baseUrl == null || baseUrl.isEmpty()) {
                baseUrl = "http://localhost:8080/api/v1";
            }
            return baseUrl + "/uploads/" + fileName;
        } catch (IOException e) {
            log.error("File upload failed", e);
            throw new RuntimeException("파일 업로드에 실패했습니다.", e);
        }
    }
}

