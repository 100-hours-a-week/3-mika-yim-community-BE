package com.mika.ktdcloud.community.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "post_images")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostImage extends AbstractAuditable{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false)
    private String originalUrl;

    @Column(nullable = false)
    private Integer imageOrder;

    @Column(name = "is_representative", nullable = false)
    private boolean isRepresentative = false;

    @Builder
    public PostImage(Post post, String originalUrl, Integer imageOrder) {
        this.post = post;
        this.originalUrl = originalUrl;
        this.imageOrder = imageOrder;
        this.isRepresentative = false;
    }

    protected void setPost(Post post) {
        this.post = post;
    }

    protected void setRepresentative(boolean isRepresentative) {
        this.isRepresentative = isRepresentative;
    }

    public void updateImageUrl(String imageUrl) {
        this.originalUrl = imageUrl;
    }

    @Override
    public void softDelete() {
        super.softDelete();
    }
}
