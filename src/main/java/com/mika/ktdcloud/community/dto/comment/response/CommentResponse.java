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
    private boolean hasChildren; // 대댓글 여부

    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    @Builder
    public CommentResponse(
            Long id,
            String content,
            Long postId,
            String authorNickname,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.content = content;
        this.postId = postId;
        this.authorNickname = authorNickname;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
