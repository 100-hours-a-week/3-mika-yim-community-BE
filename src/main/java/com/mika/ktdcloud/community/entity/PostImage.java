package com.mika.ktdcloud.community.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
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

    @Column(nullable = false)
    private String imageUrl;
    @Column(nullable = false)
    private Integer imageOrder;
    @Column(name = "is_representative", nullable = false)
    private boolean isRepresentative;

    // 양방향 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    public static PostImage create(String imageUrl, Integer imageOrder, boolean isRepresentative) {
        PostImage postImage = new PostImage();
        postImage.imageUrl = imageUrl;
        postImage.imageOrder = imageOrder;
        postImage.isRepresentative = isRepresentative;
        return postImage;
    }

    // Post의 addImage()를 통해서만 호출되도록 protected로 제한
    protected void setPost(Post post) {
        this.post = post; // post_id 저장
    }
}
