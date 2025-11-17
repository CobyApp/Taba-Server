package com.taba.notification.service;

import com.taba.common.util.SecurityUtil;
import com.taba.notification.dto.NotificationDto;
import com.taba.notification.entity.Notification;
import com.taba.notification.repository.NotificationRepository;
import com.taba.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public Page<NotificationDto> getNotifications(Pageable pageable, Notification.NotificationCategory category) {
        User currentUser = SecurityUtil.getCurrentUser();
        if (currentUser == null) {
            throw new com.taba.common.exception.BusinessException(com.taba.common.exception.ErrorCode.UNAUTHORIZED);
        }

        Page<Notification> notifications = category != null
                ? notificationRepository.findByUserIdAndCategory(currentUser.getId(), category, pageable)
                : notificationRepository.findByUserId(currentUser.getId(), pageable);

        return notifications.map(this::toDto);
    }

    @Transactional
    public NotificationDto markAsRead(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new com.taba.common.exception.BusinessException(com.taba.common.exception.ErrorCode.NOTIFICATION_NOT_FOUND));

        notification.markAsRead();
        notification = notificationRepository.save(notification);
        return toDto(notification);
    }

    @Transactional
    public int markAllAsRead() {
        User currentUser = SecurityUtil.getCurrentUser();
        if (currentUser == null) {
            throw new com.taba.common.exception.BusinessException(com.taba.common.exception.ErrorCode.UNAUTHORIZED);
        }

        // TODO: 배치 업데이트로 최적화
        Page<Notification> unreadNotifications = notificationRepository.findByUserId(currentUser.getId(), 
                org.springframework.data.domain.Pageable.unpaged());
        
        int count = 0;
        for (Notification notification : unreadNotifications) {
            if (!notification.getIsRead()) {
                notification.markAsRead();
                notificationRepository.save(notification);
                count++;
            }
        }
        return count;
    }

    @Transactional
    public void deleteNotification(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new com.taba.common.exception.BusinessException(com.taba.common.exception.ErrorCode.NOTIFICATION_NOT_FOUND));

        notificationRepository.delete(notification);
    }

    private NotificationDto toDto(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .subtitle(notification.getSubtitle())
                .time(notification.getCreatedAt())
                .category(notification.getCategory())
                .isUnread(notification.getIsRead())
                .relatedId(notification.getRelatedId())
                .build();
    }
}

