package com.mika.ktdcloud.community.mapper;

import com.mika.ktdcloud.community.dto.comment.request.CommentCreateRequest;
import com.mika.ktdcloud.community.dto.comment.response.CommentResponse;
import com.mika.ktdcloud.community.entity.Comment;
import com.mika.ktdcloud.community.entity.Post;
import com.mika.ktdcloud.community.entity.User;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    public Comment toEntity(CommentCreateRequest request, Post post, User author) {
        return Comment.create(request.getContent(), post, author);
    }

    public CommentResponse toResponse(Comment comment, boolean isAuthor) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .postId(comment.getPost().getId())
                .authorNickname(comment.getAuthor().getNickname())
                .authorProfileImageUrl(comment.getAuthor().getProfileImageUrl())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .deletedAt(comment.getDeletedAt())
                .isAuthor(isAuthor)
                .build();
    }
}
