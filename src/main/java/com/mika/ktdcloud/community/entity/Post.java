package com.mika.ktdcloud.community.entity;

import com.mika.ktdcloud.community.dto.post.request.PostCreateRequest;
import com.mika.ktdcloud.community.dto.post.request.PostUpdateRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

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

    @OneToMany(mappedBy = "post", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @OrderBy("imageOrder ASC") // 항상 이미지 순서대로 정렬
    @SQLRestriction("deleted_at IS NULL")
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

    public void update(PostUpdateRequest request, List<PostImage> newImages) {
        if (request.getTitle() != null) this.title = request.getTitle();
        if (request.getContent() != null) this.content = request.getContent();

        // 메모리상의 컬렉션 비우기
        //    (orphanRemoval=false이므로 DB에서 삭제되지 않음. 안전함.)
        this.images.clear();

        // 새로운 이미지들 추가
        if (newImages != null) {
            for (PostImage newImage : newImages) {
                this.addImage(newImage);
            }
        }
        // 썸네일 재설정
        String newThumbnailUrl = request.getThumbnailUrl(); // DTO에서 요청한 썸네일 URL
        PostImage thumbnail = null;

        if (newThumbnailUrl != null && !newThumbnailUrl.isBlank()) {
            thumbnail = this.images.stream()
                    .filter(img -> img.getOriginalUrl().equals(newThumbnailUrl))
                    .findFirst()
                    .orElse(null);
        }

        if (thumbnail == null && !newImages.isEmpty()) {
            thumbnail = newImages.get(0); // 새 이미지 중 첫 번째를 썸네일로
        }

        this.setThumbnail(thumbnail); // 썸네일 설정 메서드 호출
    }

    public void addImage(PostImage image) {
        this.images.add(image);
        image.setPost(this);
    }

    public void setThumbnail(PostImage image) {
        if(image == null) {
            this.thumbnailUrl = null;
            for (PostImage img : this.images) {
                img.setRepresentative(false);
            }
        } else {
            this.thumbnailUrl = image.getOriginalUrl();
            for (PostImage img : this.images) {
                img.setRepresentative(img.equals(image));
            }
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
