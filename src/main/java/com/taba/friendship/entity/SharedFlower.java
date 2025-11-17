package com.taba.friendship.entity;

import com.taba.letter.entity.Letter;
import com.taba.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "shared_flowers", indexes = {
    @Index(name = "idx_friendship", columnList = "friendship_id"),
    @Index(name = "idx_sent_by", columnList = "sent_by_user_id"),
    @Index(name = "idx_sent_at", columnList = "sent_at"),
    @Index(name = "idx_is_read", columnList = "is_read")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SharedFlower {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "letter_id", nullable = false)
    private Letter letter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friendship_id", nullable = false)
    @Setter
    private Friendship friendship;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sent_by_user_id", nullable = false)
    private User sentByUser;

    @Column(name = "seed_id", nullable = false, length = 50)
    private String seedId;

    @Column(name = "energy", columnDefinition = "DECIMAL(3,2) DEFAULT 0.5")
    private BigDecimal energy = new BigDecimal("0.5");

    @Column(name = "is_read")
    private Boolean isRead = false;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.sentAt == null) {
            this.sentAt = LocalDateTime.now();
        }
    }

    @Builder
    public SharedFlower(Letter letter, User sentByUser, String seedId, BigDecimal energy) {
        this.letter = letter;
        this.sentByUser = sentByUser;
        this.seedId = seedId;
        this.energy = energy != null ? energy : new BigDecimal("0.5");
        this.sentAt = LocalDateTime.now();
    }

    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }
}

