package com.taba.common.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${fcm.service-account-key-path:}")
    private String serviceAccountKeyPath;

    @Value("${fcm.service-account-key-classpath:firebase-service-account.json}")
    private String serviceAccountKeyClasspath;

    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseOptions options = buildFirebaseOptions();
                FirebaseApp.initializeApp(options);
                log.info("Firebase initialized successfully");
            } else {
                log.info("Firebase already initialized");
            }
        } catch (IOException e) {
            log.error("Failed to initialize Firebase: {}", e.getMessage(), e);
        }
    }

    private FirebaseOptions buildFirebaseOptions() throws IOException {
        InputStream serviceAccount = getServiceAccountInputStream();
        
        if (serviceAccount == null) {
            throw new IOException("Firebase service account key file not found. " +
                    "Please set fcm.service-account-key-path or place firebase-service-account.json in classpath");
        }

        return FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
    }

    private InputStream getServiceAccountInputStream() throws IOException {
        // 1. 파일 경로가 설정되어 있으면 파일 시스템에서 읽기
        if (serviceAccountKeyPath != null && !serviceAccountKeyPath.isEmpty()) {
            try {
                return new FileInputStream(serviceAccountKeyPath);
            } catch (IOException e) {
                log.warn("Failed to read Firebase service account key from path: {}", serviceAccountKeyPath);
            }
        }

        // 2. 클래스패스에서 읽기
        try {
            ClassPathResource resource = new ClassPathResource(serviceAccountKeyClasspath);
            if (resource.exists()) {
                return resource.getInputStream();
            }
        } catch (IOException e) {
            log.warn("Failed to read Firebase service account key from classpath: {}", serviceAccountKeyClasspath);
        }

        return null;
    }

    @Bean
    public FirebaseMessaging firebaseMessaging() {
        return FirebaseMessaging.getInstance();
    }
}

