package com.taba.notification.service;

import com.taba.common.util.SecurityUtil;
import com.taba.notification.dto.NotificationDto;
import com.taba.notification.entity.Notification;
import com.taba.notification.repository.NotificationRepository;
import com.taba.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.persistence.EntityManager;
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
    private final EntityManager entityManager;

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

        User user = notification.getUser();
        boolean wasUnread = notification.getIsRead() == null || !notification.getIsRead();

        notification.markAsRead();
        notification = notificationRepository.save(notification);
        entityManager.flush();

        // 읽지 않았던 알림을 읽음 처리한 경우에만 뱃지 업데이트
        if (wasUnread) {
            sendBadgeUpdateIfNeeded(user);
        }

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
        entityManager.flush();

        // 읽음 처리한 알림이 있는 경우 뱃지 업데이트
        if (count > 0) {
            sendBadgeUpdateIfNeeded(currentUser);
        }

        return count;
    }

    @Transactional
    public void deleteNotification(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new com.taba.common.exception.BusinessException(com.taba.common.exception.ErrorCode.NOTIFICATION_NOT_FOUND));

        User user = notification.getUser();
        boolean wasUnread = notification.getIsRead() == null || !notification.getIsRead();

        notificationRepository.delete(notification);
        entityManager.flush();

        // 읽지 않았던 알림을 삭제한 경우에만 뱃지 업데이트
        if (wasUnread) {
            sendBadgeUpdateIfNeeded(user);
        }
    }

    /**
     * 읽지 않은 알림 개수 조회 (앱 뱃지 숫자용)
     * 
     * @return 읽지 않은 알림 개수
     */
    @Transactional(readOnly = true)
    public long getUnreadCount() {
        User currentUser = SecurityUtil.getCurrentUser();
        if (currentUser == null) {
            throw new com.taba.common.exception.BusinessException(com.taba.common.exception.ErrorCode.UNAUTHORIZED);
        }
        return notificationRepository.countUnreadByUserId(currentUser.getId());
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
        // 저장 후 즉시 flush하여 DB에 반영 (알림 개수 계산 정확성을 위해)
        entityManager.flush();
        log.info("Notification created: {} for user: {}", notification.getId(), user.getId());

        // FCM 푸시 발송 (푸시 알림이 활성화되어 있고 FCM 토큰이 있는 경우)
        if (user.getPushNotificationEnabled() != null && user.getPushNotificationEnabled() 
            && user.getFcmToken() != null && !user.getFcmToken().isEmpty()) {
            try {
                // 읽지 않은 알림 개수 계산 (앱 뱃지 숫자) - 새로 생성된 알림 포함
                long unreadCount = notificationRepository.countUnreadByUserId(user.getId());
                
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
                        data,
                        (int) unreadCount
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

    /**
     * 뱃지 숫자를 현재 읽지 않은 알림 개수로 동기화
     * 앱이 포그라운드로 올라오거나 알림 목록 화면 진입 시 호출
     * 
     * @return 읽지 않은 알림 개수
     */
    @Transactional(readOnly = true)
    public long syncBadge() {
        User currentUser = SecurityUtil.getCurrentUser();
        if (currentUser == null) {
            throw new com.taba.common.exception.BusinessException(com.taba.common.exception.ErrorCode.UNAUTHORIZED);
        }
        
        // 현재 읽지 않은 알림 개수 계산
        long unreadCount = notificationRepository.countUnreadByUserId(currentUser.getId());
        
        // 뱃지 업데이트 푸시 전송 (읽지 않은 알림 개수로 동기화)
        sendBadgeUpdateIfNeeded(currentUser);
        
        return unreadCount;
    }

    /**
     * 뱃지 업데이트 푸시 알림 발송 (필요한 경우에만)
     * 
     * @param user 사용자
     */
    private void sendBadgeUpdateIfNeeded(User user) {
        // 푸시 알림이 활성화되어 있고 FCM 토큰이 있는 경우에만 전송
        if (user.getPushNotificationEnabled() != null && user.getPushNotificationEnabled() 
            && user.getFcmToken() != null && !user.getFcmToken().isEmpty()) {
            try {
                // 현재 읽지 않은 알림 개수 계산
                long unreadCount = notificationRepository.countUnreadByUserId(user.getId());
                
                // 뱃지 업데이트 푸시 전송
                boolean sent = fcmService.sendBadgeUpdate(user.getFcmToken(), (int) unreadCount);
                
                if (sent) {
                    log.info("Badge update sent successfully to user: {} (badge: {})", user.getId(), unreadCount);
                } else {
                    log.warn("Failed to send badge update to user: {}", user.getId());
                }
            } catch (Exception e) {
                log.error("Error sending badge update to user: {}", user.getId(), e);
                // 뱃지 업데이트 실패해도 계속 진행
            }
        }
    }

    private NotificationDto toDto(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .subtitle(notification.getSubtitle())
                .time(notification.getCreatedAt())
                .category(notification.getCategory())
                .isUnread(notification.getIsRead() == null || !notification.getIsRead())
                .relatedId(notification.getRelatedId())
                .build();
    }
}

