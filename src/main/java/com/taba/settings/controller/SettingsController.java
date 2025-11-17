package com.taba.settings.controller;

import com.taba.common.dto.ApiResponse;
import com.taba.common.util.SecurityUtil;
import com.taba.user.entity.User;
import com.taba.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final UserRepository userRepository;

    @GetMapping("/push-notification")
    public ResponseEntity<ApiResponse<PushNotificationResponse>> getPushNotificationSettings() {
        User user = com.taba.common.util.SecurityUtil.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(
                new PushNotificationResponse(user.getPushNotificationEnabled())));
    }

    @PutMapping("/push-notification")
    public ResponseEntity<ApiResponse<PushNotificationResponse>> updatePushNotificationSettings(
            @RequestBody UpdatePushNotificationRequest request) {
        User user = com.taba.common.util.SecurityUtil.getCurrentUser();
        user.updatePushNotificationEnabled(request.getEnabled());
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success(
                new PushNotificationResponse(user.getPushNotificationEnabled())));
    }

    @GetMapping("/language")
    public ResponseEntity<ApiResponse<LanguageResponse>> getLanguageSettings() {
        User user = com.taba.common.util.SecurityUtil.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(
                new LanguageResponse(user.getLanguage())));
    }

    @PutMapping("/language")
    public ResponseEntity<ApiResponse<LanguageResponse>> updateLanguageSettings(
            @RequestBody UpdateLanguageRequest request) {
        User user = com.taba.common.util.SecurityUtil.getCurrentUser();
        user.updateLanguage(request.getLanguage());
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success(
                new LanguageResponse(user.getLanguage())));
    }

    @lombok.Getter
    @lombok.AllArgsConstructor
    static class PushNotificationResponse {
        private Boolean enabled;
    }

    @lombok.Getter
    @lombok.Setter
    static class UpdatePushNotificationRequest {
        private Boolean enabled;
    }

    @lombok.Getter
    @lombok.AllArgsConstructor
    static class LanguageResponse {
        private String language;
    }

    @lombok.Getter
    @lombok.Setter
    static class UpdateLanguageRequest {
        private String language;
    }
}

