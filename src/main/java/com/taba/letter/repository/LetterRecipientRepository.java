package com.taba.letter.repository;

import com.taba.letter.entity.LetterRecipient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LetterRecipientRepository extends JpaRepository<LetterRecipient, String> {
    
    @Query("SELECT lr FROM LetterRecipient lr WHERE lr.letter.id = :letterId AND lr.user.id = :userId AND lr.deletedAt IS NULL")
    Optional<LetterRecipient> findByLetterIdAndUserId(@Param("letterId") String letterId, @Param("userId") String userId);
    
    @Query("SELECT COUNT(lr) FROM LetterRecipient lr WHERE lr.letter.id = :letterId AND lr.deletedAt IS NULL")
    long countByLetterId(@Param("letterId") String letterId);
    
    @Query("SELECT COUNT(lr) FROM LetterRecipient lr WHERE lr.user.id = :userId AND lr.isRead = false AND lr.deletedAt IS NULL")
    long countUnreadByUserId(@Param("userId") String userId);
    
    /**
     * 특정 편지를 읽은 모든 사용자 조회
     */
    @Query("SELECT lr FROM LetterRecipient lr WHERE lr.letter.id = :letterId AND lr.deletedAt IS NULL ORDER BY lr.readAt DESC")
    List<LetterRecipient> findAllByLetterId(@Param("letterId") String letterId);
    
    /**
     * 사용자가 읽은 공개 편지 목록 조회
     */
    @Query("SELECT lr FROM LetterRecipient lr WHERE lr.user.id = :userId AND lr.letter.visibility = 'PUBLIC' AND lr.deletedAt IS NULL ORDER BY lr.readAt DESC")
    Page<LetterRecipient> findPublicLettersByUserId(@Param("userId") String userId, Pageable pageable);
}

