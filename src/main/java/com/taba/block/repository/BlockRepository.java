package com.taba.block.repository;

import com.taba.block.entity.Block;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlockRepository extends JpaRepository<Block, String> {

    /**
     * 특정 사용자가 차단한 사용자 목록 조회 (차단당한 사용자 정보 포함)
     */
    @EntityGraph(attributePaths = {"blocked"})
    @Query("SELECT b FROM Block b WHERE b.blocker.id = :blockerId AND b.deletedAt IS NULL AND b.blocked.deletedAt IS NULL")
    List<Block> findByBlockerId(@Param("blockerId") String blockerId);

    /**
     * 특정 차단 관계 조회
     */
    @Query("SELECT b FROM Block b WHERE b.blocker.id = :blockerId AND b.blocked.id = :blockedId AND b.deletedAt IS NULL")
    Optional<Block> findByBlockerIdAndBlockedId(@Param("blockerId") String blockerId, @Param("blockedId") String blockedId);

    /**
     * 차단 관계 존재 여부 확인
     */
    @Query("SELECT COUNT(b) > 0 FROM Block b WHERE b.blocker.id = :blockerId AND b.blocked.id = :blockedId AND b.deletedAt IS NULL")
    boolean existsByBlockerIdAndBlockedId(@Param("blockerId") String blockerId, @Param("blockedId") String blockedId);

    /**
     * 특정 사용자가 차단한 사용자들의 ID 목록 조회
     */
    @Query("SELECT b.blocked.id FROM Block b WHERE b.blocker.id = :blockerId AND b.deletedAt IS NULL")
    List<String> findBlockedUserIdsByBlockerId(@Param("blockerId") String blockerId);

    /**
     * 양방향 차단 관계 확인 (A가 B를 차단했거나, B가 A를 차단한 경우)
     */
    @Query("SELECT COUNT(b) > 0 FROM Block b WHERE " +
           "((b.blocker.id = :userId1 AND b.blocked.id = :userId2) OR " +
           "(b.blocker.id = :userId2 AND b.blocked.id = :userId1)) " +
           "AND b.deletedAt IS NULL")
    boolean existsBlockBetweenUsers(@Param("userId1") String userId1, @Param("userId2") String userId2);
}

