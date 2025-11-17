package com.taba.friendship.entity;

import com.taba.common.entity.BaseEntity;
import com.taba.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "friendships",
    uniqueConstraints = @UniqueConstraint(name = "uk_user_friend", columnNames = {"user_id", "friend_id"}),
    indexes = {
        @Index(name = "idx_user", columnList = "user_id"),
        @Index(name = "idx_friend", columnList = "friend_id")
    })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Friendship extends BaseEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id", nullable = false)
    private User friend;

    @Column(name = "bouquet_name", length = 100)
    private String bouquetName;

    @Column(name = "bloom_level", precision = 3, scale = 2)
    private BigDecimal bloomLevel = BigDecimal.ZERO;

    @Column(name = "trust_score")
    private Integer trustScore = 0;

    @OneToMany(mappedBy = "friendship", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SharedFlower> sharedFlowers = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }

    @Builder
    public Friendship(User user, User friend) {
        this.user = user;
        this.friend = friend;
    }

    public void updateBouquetName(String bouquetName) {
        this.bouquetName = bouquetName;
    }

    public void updateBloomLevel(BigDecimal bloomLevel) {
        this.bloomLevel = bloomLevel;
    }

    public void updateTrustScore(Integer trustScore) {
        this.trustScore = trustScore;
    }

    public void addSharedFlower(SharedFlower sharedFlower) {
        this.sharedFlowers.add(sharedFlower);
        sharedFlower.setFriendship(this);
    }
}

