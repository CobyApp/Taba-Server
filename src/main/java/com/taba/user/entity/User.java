package com.taba.user.entity;

import com.taba.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_email", columnList = "email")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "email", unique = true, nullable = false, length = 255)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "language", length = 10)
    private String language = "ko";

    @Column(name = "push_notification_enabled")
    private Boolean pushNotificationEnabled = true;

    @Column(name = "fcm_token", length = 500)
    private String fcmToken;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }

    @Builder
    public User(String email, String password, String nickname, String avatarUrl) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.avatarUrl = avatarUrl;
    }

    public void updateProfile(String nickname, String avatarUrl) {
        if (nickname != null) this.nickname = nickname;
        if (avatarUrl != null) this.avatarUrl = avatarUrl;
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void updateLanguage(String language) {
        this.language = language;
    }

    public void updatePushNotificationEnabled(Boolean enabled) {
        this.pushNotificationEnabled = enabled;
    }

    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}

