package com.mika.ktdcloud.community.dto.comment.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CommentCreateRequest {

    @NotBlank(message = "댓글을 입력해주세요.")
    @Size(max=2000, message = "댓글은 최대 2000자까지만 입력 가능합니다.")
    private String content;

    @Builder
    public CommentCreateRequest(String content) {
        this.content = content;
    }
}
