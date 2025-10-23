package com.mika.ktdcloud.community.dto.post.response;

import lombok.Getter;

@Getter
public class PostLikeResponse {
    private final int likeCount;
    private final boolean isLikedByCurrentUser;

    public PostLikeResponse(int likeCount, boolean isLikedByCurrentUser) {
        this.likeCount = likeCount;
        this.isLikedByCurrentUser = isLikedByCurrentUser;
    }
}
