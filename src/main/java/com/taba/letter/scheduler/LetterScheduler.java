package com.taba.letter.scheduler;

import com.taba.letter.entity.Letter;
import com.taba.letter.repository.LetterRepository;
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

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    @Transactional
    public void sendScheduledLetters() {
        LocalDateTime now = LocalDateTime.now();
        List<Letter> scheduledLetters = letterRepository.findScheduledLettersToSend(now);

        for (Letter letter : scheduledLetters) {
            letter.send();
            letterRepository.save(letter);
            log.info("Scheduled letter sent: {}", letter.getId());
            // TODO: 알림 발송 로직 추가
        }
    }
}

