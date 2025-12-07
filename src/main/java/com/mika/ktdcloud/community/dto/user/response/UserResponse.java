package com.mika.ktdcloud.community.dto.user.response;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
public class UserResponse {
    // 응답으로 내려주는 DTO이므로 final을 붙여 변경 불가능하게 만들어줌.
    private final Long id;
    private final String email;
    private final String nickname;
    private final String profileImageUrl;

    private final Instant createdAt;
    private final Instant updatedAt;
    private final Instant deletedAt;

    @Builder
    public UserResponse(
            Long id,
            String email,
            String nickname,
            String profileImageUrl,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt
        ) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }
}
