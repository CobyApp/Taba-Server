package com.taba.letter.scheduler;

import com.taba.letter.entity.Letter;
import com.taba.letter.repository.LetterRepository;
import com.taba.notification.entity.Notification;
import com.taba.notification.repository.NotificationRepository;
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
    private final NotificationRepository notificationRepository;

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    @Transactional
    public void sendScheduledLetters() {
        LocalDateTime now = LocalDateTime.now();
        List<Letter> scheduledLetters = letterRepository.findScheduledLettersToSend(now);

        for (Letter letter : scheduledLetters) {
            letter.send();
            letterRepository.save(letter);
            log.info("Scheduled letter sent: {}", letter.getId());
            
            // 알림 발송 로직
            if (letter.getRecipient() != null) {
                Notification notification = Notification.builder()
                        .user(letter.getRecipient())
                        .title("새로운 편지가 도착했습니다")
                        .subtitle(letter.getTitle())
                        .category(Notification.NotificationCategory.LETTER)
                        .relatedId(letter.getId())
                        .build();
                notificationRepository.save(notification);
                log.info("Notification sent to user: {} for letter: {}", 
                        letter.getRecipient().getId(), letter.getId());
            }
        }
    }
}

