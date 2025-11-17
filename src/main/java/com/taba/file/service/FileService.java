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
            // TODO: AWS S3 업로드로 변경
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir);
            
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);
            
            // TODO: 실제 URL 반환
            return "/uploads/" + fileName;
        } catch (IOException e) {
            log.error("File upload failed", e);
            throw new RuntimeException("파일 업로드에 실패했습니다.", e);
        }
    }
}

