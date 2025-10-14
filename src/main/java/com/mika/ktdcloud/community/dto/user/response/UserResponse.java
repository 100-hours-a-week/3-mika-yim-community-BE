package com.mika.ktdcloud.community.dto.user.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserResponse {
    // 응답으로 내려주는 DTO이므로 final을 붙여 변경 불가능하게 만들어줌.
    private final Long id;
    private final String email;
    private final String nickname;
    private final String profileImageUrl;

    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final LocalDateTime deletedAt;

    @Builder
    public UserResponse(
            Long id,
            String email,
            String nickname,
            String profileImageUrl,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime deletedAt
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
