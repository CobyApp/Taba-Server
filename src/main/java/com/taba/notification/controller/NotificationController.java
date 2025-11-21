package com.taba.notification.controller;

import com.taba.common.dto.ApiResponse;
import com.taba.common.util.MessageUtil;
import com.taba.common.util.SecurityUtil;
import com.taba.notification.dto.NotificationDto;
import com.taba.notification.entity.Notification;
import com.taba.notification.service.NotificationService;
import com.taba.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<NotificationDto>>> getNotifications(
            @RequestParam(required = false) Notification.NotificationCategory category,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<NotificationDto> notifications = notificationService.getNotifications(pageable, category);
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<NotificationDto>> markAsRead(@PathVariable String notificationId) {
        NotificationDto notification = notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(ApiResponse.success(notification));
    }

    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<?>> markAllAsRead() {
        int readCount = notificationService.markAllAsRead();
        User currentUser = SecurityUtil.getCurrentUser();
        String language = currentUser != null && currentUser.getLanguage() != null ? currentUser.getLanguage() : "ko";
        String message = MessageUtil.getMessage("api.notification.all_read", language);
        return ResponseEntity.ok(ApiResponse.success(
                new ReadAllResponse(readCount, message)));
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<ApiResponse<?>> deleteNotification(@PathVariable String notificationId) {
        notificationService.deleteNotification(notificationId);
        User currentUser = SecurityUtil.getCurrentUser();
        String language = currentUser != null && currentUser.getLanguage() != null ? currentUser.getLanguage() : "ko";
        String message = MessageUtil.getMessage("api.notification.deleted", language);
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @lombok.Getter
    @lombok.AllArgsConstructor
    public static class ReadAllResponse {
        private int readCount;
        private String message;
    }
}

