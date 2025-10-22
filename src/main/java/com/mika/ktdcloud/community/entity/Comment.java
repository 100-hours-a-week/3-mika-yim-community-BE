package com.mika.ktdcloud.community.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "post_comments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends AbstractAuditable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_comment_id")
    private Long id;

    @Column(nullable = false)
    private String content;

    // 양방향 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    // 단방향 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    // 정적 메서드를 생성자로
    public static Comment create(String content, Post post, User author) {
        Comment comment = new Comment();
        comment.content = content;
        comment.author = author;
        post.addComment(comment); // 편의 메소드 사용
        return comment;
    }

    // 댓글 수정
    public void update(String newContent) {
        this.content = newContent;
    }

    protected void setPost(Post post) {
        this.post = post;
    }

    @Override
    public void softDelete() {
        super.softDelete();
    }
}
