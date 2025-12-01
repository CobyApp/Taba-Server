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
    @Query("SELECT l FROM Letter l WHERE l.id = :id AND l.deletedAt IS NULL " +
           "AND l.sender.deletedAt IS NULL")
    Optional<Letter> findActiveById(@Param("id") String id);

    @EntityGraph(attributePaths = {"sender", "images"}, type = org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT l FROM Letter l WHERE l.visibility = 'PUBLIC' AND l.sentAt IS NOT NULL AND l.deletedAt IS NULL " +
           "AND l.sender.deletedAt IS NULL " +
           "AND (:languages IS NULL OR l.language IN :languages)")
    List<Letter> findPublicLettersList(@Param("languages") List<String> languages);

    @EntityGraph(attributePaths = {"sender", "images"}, type = org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT l FROM Letter l WHERE l.visibility = 'PUBLIC' AND l.sentAt IS NOT NULL AND l.deletedAt IS NULL " +
           "AND l.sender.deletedAt IS NULL " +
           "AND l.sender.id != :excludeUserId " +
           "AND (:languages IS NULL OR l.language IN :languages)")
    List<Letter> findPublicLettersExcludingUserList(
            @Param("excludeUserId") String excludeUserId,
            @Param("languages") List<String> languages);

    @EntityGraph(attributePaths = {"sender", "recipient", "images"}, type = org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT l FROM Letter l WHERE l.sender.id = :userId AND l.deletedAt IS NULL " +
           "AND l.sender.deletedAt IS NULL ORDER BY l.createdAt DESC")
    Page<Letter> findBySenderId(@Param("userId") String userId, Pageable pageable);

    @EntityGraph(attributePaths = {"sender", "recipient", "images"}, type = org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT l FROM Letter l WHERE l.recipient.id = :userId AND l.visibility = 'DIRECT' AND l.deletedAt IS NULL " +
           "AND l.sender.deletedAt IS NULL AND (l.recipient.deletedAt IS NULL OR l.recipient.id = :userId) " +
           "ORDER BY l.sentAt DESC")
    Page<Letter> findByRecipientId(@Param("userId") String userId, Pageable pageable);

    @EntityGraph(attributePaths = {"sender", "recipient", "images"}, type = org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT l FROM Letter l WHERE l.scheduledAt <= :now AND l.sentAt IS NULL AND l.deletedAt IS NULL " +
           "AND l.sender.deletedAt IS NULL")
    List<Letter> findScheduledLettersToSend(@Param("now") LocalDateTime now);

    /**
     * 친구 간 주고받은 편지 조회 (양방향)
     * - DIRECT 타입: sender가 currentUserId이고 recipient가 friendId이거나, 그 반대인 편지
     *   (공개편지에 대한 답장 포함: originalLetterId가 있는 DIRECT 편지도 포함)
     *   (공개편지 복사본 포함: 공개편지에 답장할 때 생성된 DIRECT 타입 복사본도 포함)
     * 정렬은 Pageable의 sort 파라미터로 제어 (기본값: sentAt,asc - 시간순)
     * 공개편지 복사본은 답장보다 시간상 앞서 표시됩니다 (답장 시간의 1초 전으로 설정됨).
     * 
     * EntityGraph를 사용하여 sender, recipient, images를 eagerly fetch합니다.
     */
    @EntityGraph(attributePaths = {"sender", "recipient", "images"}, type = org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT DISTINCT l FROM Letter l " +
           "WHERE (" +
           "  ((l.sender.id = :currentUserId AND l.recipient.id = :friendId) OR " +
           "   (l.sender.id = :friendId AND l.recipient.id = :currentUserId)) " +
           "  AND l.visibility = 'DIRECT' " +
           "  AND (l.sentAt IS NOT NULL OR (l.scheduledAt IS NOT NULL AND l.sentAt IS NULL)) " +
           "  AND l.deletedAt IS NULL " +
           "  AND l.sender.deletedAt IS NULL " +
           "  AND (l.recipient IS NULL OR l.recipient.deletedAt IS NULL)" +
           ")")
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
           "AND l.sender.deletedAt IS NULL " +
           "AND (l.isRead IS NULL OR l.isRead = false)")
    long countUnreadLettersBetweenFriends(
            @Param("currentUserId") String currentUserId,
            @Param("friendId") String friendId);

    /**
     * 공개편지에 대한 가장 빠른 답장 조회
     * originalLetterId가 공개편지 ID와 일치하는 답장 중 가장 빠른 것을 찾습니다.
     * 양방향으로 조회 (내가 보낸 답장 또는 친구가 보낸 답장)
     */
    @Query("SELECT reply FROM Letter reply " +
           "WHERE ((reply.sender.id = :currentUserId AND reply.recipient.id = :friendId) OR " +
           "       (reply.sender.id = :friendId AND reply.recipient.id = :currentUserId)) " +
           "AND reply.visibility = 'DIRECT' " +
           "AND reply.originalLetterId = :publicLetterId " +
           "AND reply.sentAt IS NOT NULL " +
           "AND reply.deletedAt IS NULL " +
           "AND reply.sender.deletedAt IS NULL " +
           "ORDER BY reply.sentAt ASC")
    List<Letter> findEarliestReplyToPublicLetter(
            @Param("publicLetterId") String publicLetterId,
            @Param("currentUserId") String currentUserId,
            @Param("friendId") String friendId);

    /**
     * 공개편지 복사본이 이미 존재하는지 확인
     * originalLetterId가 공개편지 ID이고, sender와 recipient가 일치하는 DIRECT 편지가 있는지 확인
     */
    @Query("SELECT COUNT(l) > 0 FROM Letter l " +
           "WHERE l.originalLetterId = :originalLetterId " +
           "AND l.visibility = 'DIRECT' " +
           "AND l.sender.id = :senderId " +
           "AND l.recipient.id = :recipientId " +
           "AND l.deletedAt IS NULL")
    boolean existsByOriginalLetterIdAndSenderAndRecipient(
            @Param("originalLetterId") String originalLetterId,
            @Param("senderId") String senderId,
            @Param("recipientId") String recipientId);

    /**
     * 사용자가 받은 읽지 않은 DIRECT 편지 개수 조회
     */
    @Query("SELECT COUNT(l) FROM Letter l WHERE " +
           "l.recipient.id = :userId " +
           "AND l.visibility = 'DIRECT' " +
           "AND l.sentAt IS NOT NULL " +
           "AND l.deletedAt IS NULL " +
           "AND l.sender.deletedAt IS NULL " +
           "AND (l.recipient.deletedAt IS NULL OR l.recipient.id = :userId) " +
           "AND (l.isRead IS NULL OR l.isRead = false)")
    long countUnreadDirectLettersByUserId(@Param("userId") String userId);
}

