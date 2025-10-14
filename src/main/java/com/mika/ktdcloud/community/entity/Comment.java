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

    // 자기 자신을 참조(대댓글), 양방향 연관관계 // 나중에 추가 예정
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "parent_id")
//    private Comment parent;
//    @OneToMany(mappedBy = "parent", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
//    private List<Comment> children = new ArrayList<>();

    // 정적 메서드를 생성자로
    public static Comment create(String content, Post post, User author) {
        Comment comment = new Comment();
        comment.content = content;
        comment.author = author;
        post.addComment(comment); // 편의 메소드 사용
        return comment;
    }

    // 대댓글 생성
//    public static Comment createReply(String content, User author, Comment parent) {
//        Comment reply = new Comment();
//        reply.content = content;
//        reply.author = author;
//        reply.setParent(parent); // 편의 메소드 사용
//        return reply;
//    }

    // 댓글 수정
    public void update(String newContent) {
        this.content = newContent;
    }

    protected void setPost(Post post) {
        this.post = post;
    }

//    // 대댓글 편의 메소드 // 나중에 추가 예정
//    public void setParent(Comment parent) {
//        if (parent.getParent() != null) {
//            throw new IllegalArgumentException("대댓글은 1개의 계층으로 제한됩니다.");
//        }
//
//        this.parent = parent;
//        parent.getChildren().add(this); // 동기화
//
//        parent.getPost().addComment(this); // 게시글과의 편의 메소드
//    }

    @Override
    public void softDelete() {
        super.softDelete();

//        for (Comment child: this.children) {
//            child.softDelete(); // 대댓글 삭제
//        }
    }
}
