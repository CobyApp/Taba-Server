package com.taba.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
     * @return 성공 여부
     */
    public boolean sendPushNotification(String fcmToken, String title, String body, java.util.Map<String, String> data) {
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

            // 추가 데이터가 있으면 추가
            if (data != null && !data.isEmpty()) {
                messageBuilder.putAllData(data);
            }

            Message message = messageBuilder.build();
            String response = firebaseMessaging.send(message);
            log.info("Successfully sent FCM message: {}", response);
            return true;
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send FCM message: {}", e.getMessage(), e);
            // 토큰이 유효하지 않은 경우 (예: 앱 삭제, 토큰 만료)
            if (e.getErrorCode().equals("invalid-argument") || 
                e.getErrorCode().equals("registration-token-not-registered")) {
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
     * @return 성공한 발송 수
     */
    public int sendMulticastPushNotification(
            java.util.List<String> fcmTokens, 
            String title, 
            String body, 
            java.util.Map<String, String> data) {
        if (fcmTokens == null || fcmTokens.isEmpty()) {
            return 0;
        }

        int successCount = 0;
        for (String token : fcmTokens) {
            if (sendPushNotification(token, title, body, data)) {
                successCount++;
            }
        }
        return successCount;
    }
}

