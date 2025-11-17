package com.taba.invite.repository;

import com.taba.invite.entity.InviteCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InviteCodeRepository extends JpaRepository<InviteCode, String> {
    Optional<InviteCode> findByCode(String code);

    @Query("SELECT ic FROM InviteCode ic WHERE ic.user.id = :userId AND ic.usedAt IS NULL AND ic.expiresAt > CURRENT_TIMESTAMP ORDER BY ic.createdAt DESC")
    List<InviteCode> findActiveByUserId(@Param("userId") String userId);
    
    default Optional<InviteCode> findFirstActiveByUserId(String userId) {
        List<InviteCode> codes = findActiveByUserId(userId);
        return codes.isEmpty() ? Optional.empty() : Optional.of(codes.get(0));
    }
}

