package com.taba.notification.service;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    private final FirebaseMessaging firebaseMessaging;

    /**
     * FCM 푸시 알림 발송
     * 
     * @param fcmToken FCM 토큰
     * @param title 알림 제목
     * @param body 알림 본문
     * @param data 추가 데이터 (선택사항)
     * @param badgeCount 읽지 않은 알림 개수 (앱 뱃지 숫자, null이면 0으로 설정)
     * @return 성공 여부
     */
    public boolean sendPushNotification(String fcmToken, String title, String body, 
                                       java.util.Map<String, String> data, Integer badgeCount) {
        if (fcmToken == null || fcmToken.isEmpty()) {
            log.warn("FCM token is null or empty, skipping push notification");
            return false;
        }

        try {
            Notification notification = Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();

            Message.Builder messageBuilder = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(notification);

            // 읽지 않은 알림 개수 설정 (null이면 0)
            int badge = (badgeCount != null && badgeCount >= 0) ? badgeCount : 0;

            // 데이터 맵 생성 (기존 데이터가 없으면 새로 생성)
            Map<String, String> finalData = data != null ? new HashMap<>(data) : new HashMap<>();
            // Android 뱃지 숫자를 data payload에 추가 (앱에서 뱃지 업데이트에 사용)
            finalData.put("badge", String.valueOf(badge));
            
            // 추가 데이터 추가
            if (!finalData.isEmpty()) {
                messageBuilder.putAllData(finalData);
            }

            // iOS 설정 (APNs) - 읽지 않은 알림 개수로 뱃지 설정
            ApnsConfig apnsConfig = ApnsConfig.builder()
                    .setAps(Aps.builder()
                            .setSound("default")
                            .setBadge(badge)
                            .build())
                    .build();
            messageBuilder.setApnsConfig(apnsConfig);

            // Android 설정
            AndroidConfig androidConfig = AndroidConfig.builder()
                    .setPriority(AndroidConfig.Priority.HIGH)
                    .setNotification(AndroidNotification.builder()
                            .setSound("default")
                            .setChannelId("taba_notifications")
                            .build())
                    .build();
            messageBuilder.setAndroidConfig(androidConfig);

            Message message = messageBuilder.build();
            String response = firebaseMessaging.send(message);
            log.info("Successfully sent FCM message: {}", response);
            return true;
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send FCM message: {}", e.getMessage(), e);
            // 토큰이 유효하지 않은 경우 (예: 앱 삭제, 토큰 만료)
            // 에러 코드를 문자열로 변환하여 비교
            String errorCodeStr = e.getErrorCode() != null ? e.getErrorCode().toString() : "";
            if (errorCodeStr.contains("INVALID_ARGUMENT") || 
                errorCodeStr.contains("UNREGISTERED") ||
                errorCodeStr.contains("invalid-argument") ||
                errorCodeStr.contains("registration-token-not-registered")) {
                log.warn("Invalid FCM token, should be removed: {}", fcmToken);
            }
            return false;
        } catch (Exception e) {
            log.error("Unexpected error while sending FCM message: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 여러 기기에 FCM 푸시 알림 발송 (멀티캐스트)
     * 
     * @param fcmTokens FCM 토큰 리스트
     * @param title 알림 제목
     * @param body 알림 본문
     * @param data 추가 데이터 (선택사항)
     * @param badgeCount 읽지 않은 알림 개수 (앱 뱃지 숫자, null이면 0으로 설정)
     * @return 성공한 발송 수
     */
    public int sendMulticastPushNotification(
            java.util.List<String> fcmTokens, 
            String title, 
            String body, 
            java.util.Map<String, String> data,
            Integer badgeCount) {
        if (fcmTokens == null || fcmTokens.isEmpty()) {
            return 0;
        }

        int successCount = 0;
        for (String token : fcmTokens) {
            if (sendPushNotification(token, title, body, data, badgeCount)) {
                successCount++;
            }
        }
        return successCount;
    }

    /**
     * 뱃지 숫자만 업데이트하는 silent 푸시 알림 발송
     * 알림 읽음 처리나 삭제 시 뱃지 숫자를 업데이트하기 위해 사용
     * 
     * @param fcmToken FCM 토큰
     * @param badgeCount 읽지 않은 알림 개수 (앱 뱃지 숫자)
     * @return 성공 여부
     */
    public boolean sendBadgeUpdate(String fcmToken, Integer badgeCount) {
        if (fcmToken == null || fcmToken.isEmpty()) {
            log.warn("FCM token is null or empty, skipping badge update");
            return false;
        }

        try {
            // 읽지 않은 알림 개수 설정 (null이면 0)
            int badge = (badgeCount != null && badgeCount >= 0) ? badgeCount : 0;

            // data-only 메시지로 뱃지 업데이트 (silent push)
            Map<String, String> data = new HashMap<>();
            data.put("type", "badge_update");
            data.put("badge", String.valueOf(badge));

            Message.Builder messageBuilder = Message.builder()
                    .setToken(fcmToken)
                    .putAllData(data);

            // iOS 설정 (APNs) - 뱃지만 업데이트 (content-available: 1로 silent push)
            ApnsConfig apnsConfig = ApnsConfig.builder()
                    .setAps(Aps.builder()
                            .setBadge(badge)
                            .setContentAvailable(true)
                            .build())
                    .build();
            messageBuilder.setApnsConfig(apnsConfig);

            // Android 설정 - data-only 메시지 (priority HIGH)
            AndroidConfig androidConfig = AndroidConfig.builder()
                    .setPriority(AndroidConfig.Priority.HIGH)
                    .build();
            messageBuilder.setAndroidConfig(androidConfig);

            Message message = messageBuilder.build();
            String response = firebaseMessaging.send(message);
            log.info("Successfully sent badge update: badge={}, response={}", badge, response);
            return true;
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send badge update: {}", e.getMessage(), e);
            String errorCodeStr = e.getErrorCode() != null ? e.getErrorCode().toString() : "";
            if (errorCodeStr.contains("INVALID_ARGUMENT") || 
                errorCodeStr.contains("UNREGISTERED") ||
                errorCodeStr.contains("invalid-argument") ||
                errorCodeStr.contains("registration-token-not-registered")) {
                log.warn("Invalid FCM token, should be removed: {}", fcmToken);
            }
            return false;
        } catch (Exception e) {
            log.error("Unexpected error while sending badge update: {}", e.getMessage(), e);
            return false;
        }
    }
}

