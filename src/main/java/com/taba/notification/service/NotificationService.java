package com.taba.notification.service;

import com.taba.common.util.SecurityUtil;
import com.taba.notification.dto.NotificationDto;
import com.taba.notification.entity.Notification;
import com.taba.notification.repository.NotificationRepository;
import com.taba.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final FcmService fcmService;

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

        // 배치 업데이트로 최적화
        int count = notificationRepository.markAllAsReadByUserId(currentUser.getId());
        return count;
    }

    @Transactional
    public void deleteNotification(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new com.taba.common.exception.BusinessException(com.taba.common.exception.ErrorCode.NOTIFICATION_NOT_FOUND));

        notificationRepository.delete(notification);
    }

    /**
     * 알림 생성 및 FCM 푸시 발송
     * 
     * @param user 알림을 받을 사용자
     * @param title 알림 제목
     * @param subtitle 알림 부제목
     * @param category 알림 카테고리
     * @param relatedId 관련 ID (예: 편지 ID)
     */
    @Transactional
    public void createAndSendNotification(User user, String title, String subtitle, 
                                         Notification.NotificationCategory category, String relatedId) {
        // 알림 저장
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .subtitle(subtitle)
                .category(category)
                .relatedId(relatedId)
                .build();
        notification = notificationRepository.save(notification);
        log.info("Notification created: {} for user: {}", notification.getId(), user.getId());

        // FCM 푸시 발송 (푸시 알림이 활성화되어 있고 FCM 토큰이 있는 경우)
        if (user.getPushNotificationEnabled() != null && user.getPushNotificationEnabled() 
            && user.getFcmToken() != null && !user.getFcmToken().isEmpty()) {
            try {
                Map<String, String> data = new HashMap<>();
                data.put("notificationId", notification.getId());
                data.put("category", category.name());
                if (relatedId != null) {
                    data.put("relatedId", relatedId);
                }
                
                // 딥링크 생성
                String deepLink = generateDeepLink(category, relatedId);
                if (deepLink != null && !deepLink.isEmpty()) {
                    data.put("deepLink", deepLink);
                }

                boolean sent = fcmService.sendPushNotification(
                        user.getFcmToken(),
                        title,
                        subtitle != null ? subtitle : "",
                        data
                );

                if (sent) {
                    log.info("FCM push notification sent successfully to user: {} (deepLink: {})", 
                            user.getId(), deepLink);
                } else {
                    log.warn("Failed to send FCM push notification to user: {}", user.getId());
                }
            } catch (Exception e) {
                log.error("Error sending FCM push notification to user: {}", user.getId(), e);
                // FCM 발송 실패해도 알림은 저장되었으므로 계속 진행
            }
        } else {
            log.debug("FCM push notification skipped for user: {} (enabled: {}, token: {})", 
                    user.getId(), user.getPushNotificationEnabled(), 
                    user.getFcmToken() != null ? "exists" : "null");
        }
    }

    /**
     * 카테고리와 관련 ID를 기반으로 딥링크 생성
     * 
     * @param category 알림 카테고리
     * @param relatedId 관련 ID
     * @return 딥링크 경로
     */
    private String generateDeepLink(Notification.NotificationCategory category, String relatedId) {
        if (category == null) {
            return null;
        }

        return switch (category) {
            case LETTER, REACTION -> {
                if (relatedId != null && !relatedId.isEmpty()) {
                    yield "/letter/" + relatedId;
                }
                yield null;
            }
            case FRIEND -> {
                if (relatedId != null && !relatedId.isEmpty()) {
                    yield "/bouquet/" + relatedId;
                }
                yield "/bouquet";
            }
            case SYSTEM -> "/notifications";
        };
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

