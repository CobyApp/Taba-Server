package com.taba.letter.repository;

import com.taba.letter.entity.Letter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LetterRepository extends JpaRepository<Letter, String> {
    
    @EntityGraph(attributePaths = {"sender", "recipient", "images"}, type = org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT l FROM Letter l WHERE l.id = :id AND l.deletedAt IS NULL")
    Optional<Letter> findActiveById(@Param("id") String id);

    @EntityGraph(attributePaths = {"sender", "images"}, type = org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT l FROM Letter l WHERE l.visibility = 'PUBLIC' AND l.sentAt IS NOT NULL AND l.deletedAt IS NULL " +
           "AND (:languages IS NULL OR l.language IN :languages) " +
           "ORDER BY l.sentAt DESC")
    Page<Letter> findPublicLetters(@Param("languages") List<String> languages, Pageable pageable);

    @EntityGraph(attributePaths = {"sender", "images"}, type = org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT l FROM Letter l WHERE l.visibility = 'PUBLIC' AND l.sentAt IS NOT NULL AND l.deletedAt IS NULL " +
           "AND l.sender.id != :excludeUserId " +
           "AND (:languages IS NULL OR l.language IN :languages) " +
           "ORDER BY l.sentAt DESC")
    Page<Letter> findPublicLettersExcludingUser(
            @Param("excludeUserId") String excludeUserId,
            @Param("languages") List<String> languages,
            Pageable pageable);

    @EntityGraph(attributePaths = {"sender", "recipient", "images"}, type = org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT l FROM Letter l WHERE l.sender.id = :userId AND l.deletedAt IS NULL ORDER BY l.createdAt DESC")
    Page<Letter> findBySenderId(@Param("userId") String userId, Pageable pageable);

    @EntityGraph(attributePaths = {"sender", "recipient", "images"}, type = org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT l FROM Letter l WHERE l.recipient.id = :userId AND l.visibility = 'DIRECT' AND l.deletedAt IS NULL ORDER BY l.sentAt DESC")
    Page<Letter> findByRecipientId(@Param("userId") String userId, Pageable pageable);

    @EntityGraph(attributePaths = {"sender", "recipient", "images"}, type = org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT l FROM Letter l WHERE l.scheduledAt <= :now AND l.sentAt IS NULL AND l.deletedAt IS NULL")
    List<Letter> findScheduledLettersToSend(@Param("now") LocalDateTime now);

    /**
     * 친구 간 주고받은 편지 조회 (양방향)
     * - DIRECT 타입: sender가 currentUserId이고 recipient가 friendId이거나, 그 반대인 편지
     * - PUBLIC 타입: 친구(friendId)가 작성한 공개편지 중, 내가 답장을 보낸 공개편지만 포함
     *   (답장을 보낸 시점 이전에 작성된 공개편지만 포함)
     * 정렬은 Pageable의 sort 파라미터로 제어 (기본값: sentAt,asc - 시간순)
     * 공개편지가 답장보다 시간상 앞서면 공개편지가 우선적으로 표시됩니다.
     * 
     * EntityGraph를 사용하여 sender, recipient, images를 eagerly fetch합니다.
     */
    @EntityGraph(attributePaths = {"sender", "recipient", "images"}, type = org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT DISTINCT l FROM Letter l " +
           "WHERE (" +
           "  ((l.sender.id = :currentUserId AND l.recipient.id = :friendId) OR " +
           "   (l.sender.id = :friendId AND l.recipient.id = :currentUserId)) " +
           "  AND l.visibility = 'DIRECT' " +
           ") OR (" +
           "  l.sender.id = :friendId " +
           "  AND l.visibility = 'PUBLIC' " +
           "  AND EXISTS (" +
           "    SELECT 1 FROM Letter reply " +
           "    WHERE reply.sender.id = :currentUserId " +
           "    AND reply.recipient.id = :friendId " +
           "    AND reply.visibility = 'DIRECT' " +
           "    AND reply.sentAt IS NOT NULL " +
           "    AND reply.deletedAt IS NULL " +
           "    AND reply.sentAt >= l.sentAt" +
           "  )" +
           ") " +
           "AND l.sentAt IS NOT NULL " +
           "AND l.deletedAt IS NULL")
    Page<Letter> findLettersBetweenFriends(
            @Param("currentUserId") String currentUserId,
            @Param("friendId") String friendId,
            Pageable pageable);

    /**
     * 친구 간 읽지 않은 편지 수 조회
     */
    @Query("SELECT COUNT(l) FROM Letter l WHERE " +
           "l.recipient.id = :currentUserId " +
           "AND l.sender.id = :friendId " +
           "AND l.visibility = 'DIRECT' " +
           "AND l.sentAt IS NOT NULL " +
           "AND l.deletedAt IS NULL " +
           "AND (l.isRead IS NULL OR l.isRead = false)")
    long countUnreadLettersBetweenFriends(
            @Param("currentUserId") String currentUserId,
            @Param("friendId") String friendId);
}

