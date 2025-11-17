package com.taba.letter.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "letter_images", indexes = {
    @Index(name = "idx_letter", columnList = "letter_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LetterImage {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "letter_id", nullable = false)
    @Setter
    private Letter letter;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "image_order", nullable = false)
    private Integer imageOrder;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }

    public LetterImage(String imageUrl, Integer imageOrder) {
        this.imageUrl = imageUrl;
        this.imageOrder = imageOrder;
    }
}

