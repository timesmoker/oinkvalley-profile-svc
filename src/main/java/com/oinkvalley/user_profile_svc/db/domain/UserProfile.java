package com.oinkvalley.user_profile_svc.db.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;

@Entity
@Table(name = "user_profiles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProfile {
    // 아직 빌더 안 씀 > 걍 생성자

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 64)
    private String nickname;

    @Column(length = 2000)
    @Nullable
    private String bio;

    public UserProfile(Long userId, String nickname) {
        this.userId = userId;
        this.nickname = nickname;
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void changeBio(@Nullable String bio) {
        this.bio = bio;
    }
}