package com.mika.ktdcloud.community.dto.post.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PostDetailResponse {

    private final Long id;
    private final String title;
    private final String content;
    private final String thumbnailUrl;
    private final List<String> imageUrls;
    private final String authorNickname;
    private final String authorProfileImageUrl;

    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final LocalDateTime deletedAt;

    private final Integer viewCount;
    private final Integer likeCount;
    private final Integer commentCount;

    private final boolean isLikedByCurrentUser;
    private final boolean isAuthor;

    @Builder
    public PostDetailResponse(
            Long id,
            String title,
            String content,
            String thumbnailUrl,
            List<String> imageUrls,
            String authorNickname,
            String authorProfileImageUrl,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime deletedAt,
            Integer viewCount,
            Integer likeCount,
            Integer commentCount,
            boolean isLikedByCurrentUser,
            boolean isAuthor
    ) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.thumbnailUrl = thumbnailUrl;
        this.imageUrls = imageUrls;
        this.authorNickname = authorNickname;
        this.authorProfileImageUrl = authorProfileImageUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.isLikedByCurrentUser = isLikedByCurrentUser;
        this.isAuthor = isAuthor;
    }
}