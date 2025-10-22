package com.mika.ktdcloud.community.repository;

import com.mika.ktdcloud.community.entity.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CommentRepositoryCustom {
    Slice<Comment> findTopCommentsByPostId(Long postId, Pageable pageable); // 게시글에 속한 댓글 목록 조회
}