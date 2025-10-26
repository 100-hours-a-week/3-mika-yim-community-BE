package com.mika.ktdcloud.community.repository;

import com.mika.ktdcloud.community.entity.Comment;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

import static com.mika.ktdcloud.community.entity.QComment.comment;
import static com.mika.ktdcloud.community.entity.QUser.user;

@RequiredArgsConstructor
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Comment> findTopCommentsByPostIdWithAuthor(Long postId, Pageable pageable) {
        List<Comment> comments = queryFactory
                .selectFrom(comment)
                .leftJoin(comment.author, user).fetchJoin()
                .where(comment.post.id.eq(postId))
                .where(comment.deletedAt.isNull())
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
