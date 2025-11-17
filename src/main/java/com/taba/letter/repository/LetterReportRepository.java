package com.taba.letter.repository;

import com.taba.letter.entity.LetterReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LetterReportRepository extends JpaRepository<LetterReport, String> {
    boolean existsByLetterIdAndReporterId(String letterId, String reporterId);
}

