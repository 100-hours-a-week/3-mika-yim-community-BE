package com.mika.ktdcloud.community.repository;

import com.mika.ktdcloud.community.entity.Comment;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

import static com.mika.ktdcloud.community.entity.QComment.comment;

@RequiredArgsConstructor
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Comment> findTopCommentsByPostId(Long postId, Pageable pageable) {
        List<Comment> comments = queryFactory
                .selectFrom(comment)
                .where(comment.post.id.eq(postId))
                .orderBy(comment.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNextPage = false;
        if (comments.size() > pageable.getPageSize()) {
            comments.remove(pageable.getPageSize());
            hasNextPage = true;
        }

        return new SliceImpl<>(comments, pageable, hasNextPage);
    }
}
