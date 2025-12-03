package com.taba.letter.repository;

import com.taba.letter.entity.LetterReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LetterReportRepository extends JpaRepository<LetterReport, String> {
    boolean existsByLetterIdAndReporterId(String letterId, String reporterId);
    
    /**
     * 특정 편지의 신고 수를 카운트합니다.
     */
    long countByLetterId(String letterId);
}

