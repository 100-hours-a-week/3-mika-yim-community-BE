package com.mika.ktdcloud.community.dto.comment.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponse {

    private final Long id;
    private final String content;
    private final Long postId;
    private final String authorNickname;
    private final String authorProfileImageUrl;

    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final LocalDateTime deletedAt;

    private final boolean isAuthor;

    @Builder
    public CommentResponse(
            Long id,
            String content,
            Long postId,
            String authorNickname,
            String authorProfileImageUrl,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime deletedAt,
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
