package com.oinkvalley.user_profile_svc.db.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.jspecify.annotations.Nullable;

@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 표시용 닉네임. 예전 모놀리스 {@code users.username} 과 동일 계열 (비밀번호·이메일은 auth 쪽).
     */
    @Column(nullable = false, length = 64)
    private String nickname;

    @Column(length = 2000)
    @Nullable
    private String bio;

    @Column(length = 32)
    @Nullable
    private String locale;

    protected UserProfile() {
    }

    public UserProfile(Long userId, String nickname) {
        this.userId = userId;
        this.nickname = nickname;
    }

    public Long getUserId() {
        return userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Nullable
    public String getBio() {
        return bio;
    }

    public void setBio(@Nullable String bio) {
        this.bio = bio;
    }

    @Nullable
    public String getLocale() {
        return locale;
    }

    public void setLocale(@Nullable String locale) {
        this.locale = locale;
    }
}
