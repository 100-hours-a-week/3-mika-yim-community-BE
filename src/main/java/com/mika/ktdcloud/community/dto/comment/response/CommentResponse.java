package com.mika.ktdcloud.community.dto.comment.response;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
public class CommentResponse {

    private final Long id;
    private final String content;
    private final Long postId;
    private final String authorNickname;
    private final String authorProfileImageUrl;

    private final Instant createdAt;
    private final Instant updatedAt;
    private final Instant deletedAt;

    private final boolean isAuthor;

    @Builder
    public CommentResponse(
            Long id,
            String content,
            Long postId,
            String authorNickname,
            String authorProfileImageUrl,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt,
            boolean isAuthor
    ) {
        this.id = id;
        this.content = content;
        this.postId = postId;
        this.authorNickname = authorNickname;
        this.authorProfileImageUrl = authorProfileImageUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.isAuthor = isAuthor;
    }
}
