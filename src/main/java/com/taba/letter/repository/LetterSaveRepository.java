package com.taba.letter.repository;

import com.taba.letter.entity.LetterSave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LetterSaveRepository extends JpaRepository<LetterSave, String> {
    Optional<LetterSave> findByLetterIdAndUserId(String letterId, String userId);
    boolean existsByLetterIdAndUserId(String letterId, String userId);
    long countByLetterId(String letterId);
}

