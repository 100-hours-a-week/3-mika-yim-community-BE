package com.mika.ktdcloud.community.controller;

import com.mika.ktdcloud.community.dto.comment.request.CommentCreateRequest;
import com.mika.ktdcloud.community.dto.comment.request.CommentUpdateRequest;
import com.mika.ktdcloud.community.dto.comment.response.CommentResponse;
import com.mika.ktdcloud.community.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/api/v1/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    // 댓글 생성
    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long postId,
            @RequestBody @Valid CommentCreateRequest request) {
        Long authorId = 1L; // 인증 인가 추가 후 현재 사용자 정보 가져오기
        CommentResponse response = commentService.createComment(request, postId, authorId);
        URI location = URI.create("/api/v1/post/" + postId + "/comments" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    // 댓글 수정
    @PostMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @RequestBody @Valid CommentUpdateRequest request
            // 인증 인가 추가 후 현재 사용자 정보 가져오기
            ) throws AccessDeniedException {
        Long currentUserId = 1L;
        CommentResponse response = commentService.updateComment(request, commentId, currentUserId);
        return ResponseEntity.ok(response);
    }

    // 댓글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) throws AccessDeniedException {
        Long currentUser = 1L;

        commentService.deleteComment(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
