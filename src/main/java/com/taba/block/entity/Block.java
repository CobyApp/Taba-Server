package com.taba.block.entity;

import com.taba.common.entity.BaseEntity;
import com.taba.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * 사용자 차단 엔티티
 * - blocker: 차단을 한 사용자
 * - blocked: 차단을 당한 사용자
 */
@Entity
@Table(name = "blocks",
    uniqueConstraints = @UniqueConstraint(name = "uk_blocker_blocked", columnNames = {"blocker_id", "blocked_id"}),
    indexes = {
        @Index(name = "idx_blocker", columnList = "blocker_id"),
        @Index(name = "idx_blocked", columnList = "blocked_id")
    })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Block extends BaseEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocker_id", nullable = false)
    private User blocker; // 차단을 한 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_id", nullable = false)
    private User blocked; // 차단을 당한 사용자

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }

    @Builder
    public Block(User blocker, User blocked) {
        this.blocker = blocker;
        this.blocked = blocked;
    }
}

