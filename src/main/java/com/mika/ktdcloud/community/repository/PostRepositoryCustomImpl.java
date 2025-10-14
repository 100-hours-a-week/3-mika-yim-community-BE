package com.mika.ktdcloud.community.repository;

import com.mika.ktdcloud.community.dto.post.response.PostSimpleResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.mika.ktdcloud.community.entity.QPost.post;
import static com.mika.ktdcloud.community.entity.QPostStat.postStat;
import static com.mika.ktdcloud.community.entity.QUser.user;

@RequiredArgsConstructor
public class PostRepositoryCustomImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<PostSimpleResponse> findPostsWithDetails(Pageable pageable) {
        // 게시글 목록 조회 쿼리
        List<PostSimpleResponse> content = queryFactory
                .select(Projections.constructor(PostSimpleResponse.class,
                        post.id,
                        post.title,
                        post.thumbnailUrl,
                        user.nickname,
                        post.createdAt,
                        post.updatedAt,
                        postStat.viewCount,
                        postStat.likeCount,
                        postStat.commentCount
                ))
                .from(post)
                .join(post.author, user)
                .join(post.stat, postStat)
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1) // 요청한 페이지 크기보다 하나 더 많이 조회
                .fetch();

        // 다음 페이지 존재 여부 확인
        boolean hasNextPage = false;
        if (content.size() > pageable.getPageSize()) { // 조회된 데이터 수가 요청한 페이지 수보다 많다면 다음 페이지가 있다고 판단함
            content.remove(pageable.getPageSize());
            hasNextPage = true;
        }

        return new SliceImpl<>(content, pageable, hasNextPage);
    }
}
