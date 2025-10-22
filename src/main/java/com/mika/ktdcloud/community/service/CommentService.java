package com.mika.ktdcloud.community.service;

import com.mika.ktdcloud.community.dto.comment.request.CommentCreateRequest;
import com.mika.ktdcloud.community.dto.comment.request.CommentUpdateRequest;
import com.mika.ktdcloud.community.dto.comment.response.CommentResponse;
import com.mika.ktdcloud.community.entity.Comment;
import com.mika.ktdcloud.community.entity.Post;
import com.mika.ktdcloud.community.entity.User;
import com.mika.ktdcloud.community.mapper.CommentMapper;
import com.mika.ktdcloud.community.repository.CommentRepository;
import com.mika.ktdcloud.community.repository.PostRepository;
import com.mika.ktdcloud.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    // 댓글 생성
    @Transactional
    public CommentResponse createComment(CommentCreateRequest request, Long postId, Long authorId) {
        Post post = postRepository.findWithLockById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found."));
        User author = userRepository.getReferenceById(authorId);
        Comment newComment = commentMapper.toEntity(request, post, author);
        Comment savedComment = commentRepository.save(newComment);

        post.getStat().increaseCommentCount();
        return commentMapper.toResponse(savedComment);
    }

    @Transactional(readOnly = true)
    public Slice<CommentResponse> getComment(Long postId, Pageable pageable) {
        Slice<Comment> commentSlice = commentRepository.findTopCommentsByPostId(postId, pageable);
        return commentSlice.map(commentMapper::toResponse);
    }

    // 댓글 수정
    @Transactional
    public CommentResponse updateComment(CommentUpdateRequest request, Long commentId, Long currentUserId) throws AccessDeniedException {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found."));
        if (!comment.getAuthor().getId().equals(currentUserId)) {
            throw new AccessDeniedException("Only author can update comment.");
        }
        comment.update(request.getContent());
        return commentMapper.toResponse(comment);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long id, Long currentUserId) throws AccessDeniedException {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("comment not found."));

        if (!comment.getAuthor().getId().equals(currentUserId)) {
            throw new AccessDeniedException("Only the author of this comment can delete.");
        }

        Long postId = comment.getPost().getId();
        Post post = postRepository.findWithLockById(postId)
                        .orElseThrow(() -> new IllegalArgumentException("Post not found."));

        comment.softDelete();
        post.getStat().decreaseCommentCount();
    }
}
