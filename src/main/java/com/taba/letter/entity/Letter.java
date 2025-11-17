package com.taba.letter.entity;

import com.taba.common.entity.BaseEntity;
import com.taba.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "letters", indexes = {
    @Index(name = "idx_sender", columnList = "sender_id"),
    @Index(name = "idx_recipient", columnList = "recipient_id"),
    @Index(name = "idx_visibility", columnList = "visibility"),
    @Index(name = "idx_scheduled_at", columnList = "scheduled_at"),
    @Index(name = "idx_sent_at", columnList = "sent_at"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Letter extends BaseEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id")
    private User recipient;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "preview", nullable = false, length = 500)
    private String preview;

    @Enumerated(EnumType.STRING)
    @Column(name = "flower_type", nullable = false, length = 20)
    private FlowerType flowerType;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false, length = 20)
    private Visibility visibility;

    @Column(name = "is_anonymous")
    private Boolean isAnonymous = false;

    @Column(name = "template_background", length = 50)
    private String templateBackground;

    @Column(name = "template_text_color", length = 50)
    private String templateTextColor;

    @Column(name = "template_font_family", length = 100)
    private String templateFontFamily;

    @Column(name = "template_font_size", columnDefinition = "DECIMAL(5,2)")
    private Double templateFontSize;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "likes")
    private Integer likes = 0;

    @Column(name = "views")
    private Integer views = 0;

    @Column(name = "saved_count")
    private Integer savedCount = 0;

    @OneToMany(mappedBy = "letter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LetterImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "letter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LetterLike> letterLikes = new ArrayList<>();

    @OneToMany(mappedBy = "letter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LetterSave> letterSaves = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }

    @Builder
    public Letter(User sender, User recipient, String title, String content, String preview,
                  FlowerType flowerType, Visibility visibility, Boolean isAnonymous,
                  String templateBackground, String templateTextColor, String templateFontFamily,
                  Double templateFontSize, LocalDateTime scheduledAt) {
        this.sender = sender;
        this.recipient = recipient;
        this.title = title;
        this.content = content;
        this.preview = preview;
        this.flowerType = flowerType;
        this.visibility = visibility;
        this.isAnonymous = isAnonymous;
        this.templateBackground = templateBackground;
        this.templateTextColor = templateTextColor;
        this.templateFontFamily = templateFontFamily;
        this.templateFontSize = templateFontSize;
        this.scheduledAt = scheduledAt;
    }

    public void send() {
        this.sentAt = LocalDateTime.now();
    }

    public void incrementViews() {
        this.views++;
    }

    public void incrementLikes() {
        this.likes++;
    }

    public void decrementLikes() {
        if (this.likes > 0) {
            this.likes--;
        }
    }

    public void incrementSavedCount() {
        this.savedCount++;
    }

    public void decrementSavedCount() {
        if (this.savedCount > 0) {
            this.savedCount--;
        }
    }

    public void addImage(LetterImage image) {
        this.images.add(image);
        image.setLetter(this);
    }

    public enum FlowerType {
        ROSE, TULIP, SAKURA, SUNFLOWER, DAISY, LAVENDER
    }

    public enum Visibility {
        PUBLIC, FRIENDS, DIRECT, PRIVATE
    }
}

