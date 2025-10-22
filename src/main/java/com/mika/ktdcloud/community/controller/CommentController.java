package com.mika.ktdcloud.community.controller;

import com.mika.ktdcloud.community.dto.comment.request.CommentCreateRequest;
import com.mika.ktdcloud.community.dto.comment.request.CommentUpdateRequest;
import com.mika.ktdcloud.community.dto.comment.response.CommentResponse;
import com.mika.ktdcloud.community.service.CommentService;
import com.mika.ktdcloud.community.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
        Long authorId = SecurityUtil.getCurrentUserId();
        CommentResponse response = commentService.createComment(request, postId, authorId);
        URI location = URI.create("/api/v1/post/" + postId + "/comments" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    // 게시글 ID의 댓글을 목록으로 조회
    @GetMapping
    public ResponseEntity<Slice<CommentResponse>> getComments(
            @PathVariable Long postId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Slice<CommentResponse> comments = commentService.getComment(postId, pageable);
        return ResponseEntity.ok(comments);
    }

    // 댓글 수정
    @PostMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @RequestBody @Valid CommentUpdateRequest request
            // 인증 인가 추가 후 현재 사용자 정보 가져오기
            ) throws AccessDeniedException {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        CommentResponse response = commentService.updateComment(request, commentId, currentUserId);
        return ResponseEntity.ok(response);
    }

    // 댓글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) throws AccessDeniedException {
        Long authorId = SecurityUtil.getCurrentUserId();
        commentService.deleteComment(id, authorId);
        return ResponseEntity.noContent().build();
    }
}
