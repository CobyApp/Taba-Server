package com.taba.common.controller;

import com.taba.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 헬스체크 엔드포인트
 * Docker 헬스체크 및 배포 확인용
 */
@RestController
@RequestMapping("/actuator")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, String>>> health() {
        return ResponseEntity.ok(
                ApiResponse.success(Map.of("status", "UP"), "Service is running")
        );
    }
}

