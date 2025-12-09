package com.mika.ktdcloud.community.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Table(name = "refresh_tokens")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String tokenValue; // Refresh Token 값

    @Column(nullable = false)
    private Instant expiresAt;

    private boolean revoked = false;

    public RefreshToken(User user, String tokenValue, Instant expiresAt, boolean revoked) {
        this.user = user;
        this.tokenValue = tokenValue;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
    }

    // 토큰 무효화
    public void revoke() {
        this.revoked = true;
    }
}
