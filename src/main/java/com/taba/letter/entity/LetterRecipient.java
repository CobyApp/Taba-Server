package com.taba.letter.entity;

import com.taba.common.entity.BaseEntity;
import com.taba.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "letter_recipients", indexes = {
    @Index(name = "idx_letter_recipient", columnList = "letter_id, user_id", unique = true),
    @Index(name = "idx_user_letter", columnList = "user_id, letter_id"),
    @Index(name = "idx_read_at", columnList = "read_at")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LetterRecipient extends BaseEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "letter_id", nullable = false)
    private Letter letter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public void setLetter(Letter letter) {
        this.letter = letter;
    }

    @Column(name = "is_read")
    private Boolean isRead = false;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }

    @Builder
    public LetterRecipient(Letter letter, User user) {
        this.letter = letter;
        this.user = user;
    }

    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }
}

