package com.mika.ktdcloud.community.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "post_likes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "post_id"}) // 한 사용자가 한 게시글에 좋아요를 한 번만 누를 수 있음
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostLike extends AbstractAuditable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_likes_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    public static PostLike create(User user, Post post) {
        PostLike postLike = new PostLike();
        postLike.user = user;
        postLike.post = post;
        return postLike;
    }

}
