package com.mika.ktdcloud.community.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "post_stats")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostStat {

    @Id
    private Long id; // Post의 ID를 그대로 기본 키로 사용

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(nullable = false)
    private int viewCount = 0;
    @Column(nullable = false)
    private int likeCount = 0;
    @Column(nullable = false)
    private int commentCount = 0;

    public PostStat(Post post) {
        this.post = post;
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        if (this.likeCount > 0) this.likeCount--;
    }

    public void increaseCommentCount() {
        this.commentCount++;
    }

    public void decreaseCommentCount() {
        if (this.commentCount > 0) this.commentCount--;
    }

}
