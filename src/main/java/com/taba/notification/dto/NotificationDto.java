package com.taba.notification.dto;

import com.taba.notification.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private String id;
    private String title;
    private String subtitle;
    private LocalDateTime time;
    private Notification.NotificationCategory category;
    private Boolean isUnread;
    private String relatedId;
}

