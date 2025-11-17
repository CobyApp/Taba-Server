package com.taba.letter.repository;

import com.taba.letter.entity.Letter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LetterRepository extends JpaRepository<Letter, String> {
    
    @Query("SELECT l FROM Letter l WHERE l.id = :id AND l.deletedAt IS NULL")
    Optional<Letter> findActiveById(@Param("id") String id);

    @Query("SELECT l FROM Letter l WHERE l.visibility = 'PUBLIC' AND l.sentAt IS NOT NULL AND l.deletedAt IS NULL ORDER BY l.sentAt DESC")
    Page<Letter> findPublicLetters(Pageable pageable);

    @Query("SELECT l FROM Letter l WHERE l.visibility = 'PUBLIC' AND l.sentAt IS NOT NULL AND l.deletedAt IS NULL AND l.sender.id != :excludeUserId ORDER BY l.sentAt DESC")
    Page<Letter> findPublicLettersExcludingUser(@Param("excludeUserId") String excludeUserId, Pageable pageable);

    @Query("SELECT l FROM Letter l WHERE l.sender.id = :userId AND l.deletedAt IS NULL ORDER BY l.createdAt DESC")
    Page<Letter> findBySenderId(@Param("userId") String userId, Pageable pageable);

    @Query("SELECT l FROM Letter l WHERE l.recipient.id = :userId AND l.visibility = 'DIRECT' AND l.deletedAt IS NULL ORDER BY l.sentAt DESC")
    Page<Letter> findByRecipientId(@Param("userId") String userId, Pageable pageable);

    @Query("SELECT l FROM Letter l WHERE l.scheduledAt <= :now AND l.sentAt IS NULL AND l.deletedAt IS NULL")
    List<Letter> findScheduledLettersToSend(@Param("now") LocalDateTime now);
}

