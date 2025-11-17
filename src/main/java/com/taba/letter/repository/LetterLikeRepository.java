package com.taba.letter.repository;

import com.taba.letter.entity.LetterLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LetterLikeRepository extends JpaRepository<LetterLike, String> {
    Optional<LetterLike> findByLetterIdAndUserId(String letterId, String userId);
    boolean existsByLetterIdAndUserId(String letterId, String userId);
    long countByLetterId(String letterId);
}

