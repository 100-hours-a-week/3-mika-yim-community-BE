package com.mika.ktdcloud.community.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "refresh_tokens")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id")
    private Long id;

    // User와 일대일 관계
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String tokenValue; // 실제 Refresh Token 값

    public RefreshToken(User user, String tokenValue) {
        this.user = user;
        this.tokenValue = tokenValue;
    }

    // refresh 토큰 값 업데이트 메서드
    public void updateTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }
}
