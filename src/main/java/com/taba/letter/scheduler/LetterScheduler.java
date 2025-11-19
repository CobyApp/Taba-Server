package com.taba.letter.scheduler;

import com.taba.letter.entity.Letter;
import com.taba.letter.repository.LetterRepository;
import com.taba.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LetterScheduler {

    private final LetterRepository letterRepository;
    private final NotificationService notificationService;

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    @Transactional
    public void sendScheduledLetters() {
        LocalDateTime now = LocalDateTime.now();
        List<Letter> scheduledLetters = letterRepository.findScheduledLettersToSend(now);

        if (scheduledLetters.isEmpty()) {
            log.debug("No scheduled letters to send at {}", now);
            return;
        }

        log.info("Found {} scheduled letter(s) to send", scheduledLetters.size());

        for (Letter letter : scheduledLetters) {
            try {
            letter.send();
            letterRepository.save(letter);
                log.info("Scheduled letter sent: letterId={}, scheduledAt={}, sentAt={}", 
                        letter.getId(), letter.getScheduledAt(), letter.getSentAt());
            
            // 알림 발송 로직 (FCM 푸시 포함)
            if (letter.getRecipient() != null) {
                notificationService.createAndSendNotification(
                        letter.getRecipient(),
                        "새로운 편지가 도착했습니다",
                        letter.getTitle(),
                        com.taba.notification.entity.Notification.NotificationCategory.LETTER,
                        letter.getId()
                );
                    log.debug("Notification sent for scheduled letter: letterId={}, recipientId={}", 
                            letter.getId(), letter.getRecipient().getId());
                }
            } catch (Exception e) {
                log.error("Failed to send scheduled letter: letterId={}, error={}", 
                        letter.getId(), e.getMessage(), e);
            }
        }
    }
}

