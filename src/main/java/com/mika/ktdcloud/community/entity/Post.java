package com.mika.ktdcloud.community.entity;

import com.mika.ktdcloud.community.dto.post.request.PostCreateRequest;
import com.mika.ktdcloud.community.dto.post.request.PostUpdateRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "posts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends AbstractAuditable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("imageOrder ASC") // 항상 이미지 순서대로 정렬
    private List<PostImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @OrderBy("createdAt ASC")
    private List<Comment> comments = new ArrayList<>();

    @OneToOne(mappedBy = "post", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private PostStat stat;

    public static Post create(PostCreateRequest request, User author) {
        Post post = new Post();
        post.title = request.getTitle();
        post.content = request.getContent();
        post.confirmAuthor(author);
        post.addStat(new PostStat(post));
        return post;
    }

    public void update(PostUpdateRequest request) {
        if (request.getTitle() != null) this.title = request.getTitle();
        if (request.getContent() != null) this.content = request.getContent();
    }

    public void addImage(PostImage image) {
        this.images.add(image);
        image.setPost(this);
    }

    public void setThumbnail(PostImage image) {
        this.thumbnailUrl = image.getSmallUrl();

        for (PostImage img : this.images) {
            img.setRepresentative(img.equals(image));
        }
    }

    public void confirmAuthor(User author) {
        this.author = author;
    }

    // Post-Comment 편의 메소드
    public void addComment(Comment comment) {
        this.comments.add(comment);
        comment.setPost(this);
    }

    // Post-PostStat 편의 메소드
    public void addStat(PostStat stat) {
        this.stat = stat;
    }

    @Override
    public void softDelete() {
        super.softDelete();

        for (PostImage image : this.images) {
            image.softDelete();
        }

        for (Comment comment : this.comments) {
            comment.softDelete();
        }
    }
}
