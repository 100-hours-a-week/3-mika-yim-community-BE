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
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 생성
public class Post extends AbstractAuditable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Column(nullable = false)
    private String title;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    // 다대일 단방향 관계
    @ManyToOne(fetch = FetchType.LAZY) // 성능 최적화를 위해 지연 로딩 설정
    @JoinColumn(name = "user_id", nullable = false) // posts 테이블의 외래 키 컬럼
    private User author; // 작성자 (User 엔티티 참조)

    // 양방향 관계
    @OneToMany(mappedBy = "post", cascade = {CascadeType.PERSIST, CascadeType.MERGE}) // 저장 병합 작업이 함께 전이됨
    @OrderBy("imageOrder ASC") // 조회할 때 항상 imageOrder의 오름차순으로 정렬을 기본값으로 설정
    private List<PostImage> images = new ArrayList<>();
    // 양방향 관계
    @OneToMany(mappedBy = "post", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @OrderBy("createdAt ASC") // 댓글 역시 생성시간을 기준으로 오름차순으로 정렬을 기본값으로 설정
    private List<Comment> comments = new ArrayList<>();

    // 엔티티 분리
    @OneToOne(mappedBy = "post", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private PostStat stat;

    // 정적 펙토리 메서드를 사용해서 생성자를 대체함
    public static Post create(PostCreateRequest request, User author) {
        Post post = new Post();
        post.title = request.getTitle();
        post.content = request.getContent();
        post.confirmAuthor(author); // 편의 메서드 사용
        if (request.getImageUrls() != null) { // 이미지가 있을 때만 실행하면 됨
            post.updateImages(request.getImageUrls(), request.getThumbnailUrl());
        }
        post.addStat(new PostStat(post));
        return post;
    }

    public void update(PostUpdateRequest request) {
        if (request.getTitle() != null) this.title = request.getTitle();
        if (request.getContent() != null) this.content = request.getContent();
        if (request.getImageUrls() != null) {
            updateImages(request.getImageUrls(), request.getThumbnailUrl());
        }
    }

    public void updateImages(List<String> newImageUrls, String newThumbnailUrl) {

        String requestedThumbnailUrl = newThumbnailUrl;

        if ((requestedThumbnailUrl == null || requestedThumbnailUrl.isBlank())
                && (newImageUrls != null && !newImageUrls.isEmpty())) {
            requestedThumbnailUrl = newImageUrls.getFirst();
        } // 썸네일을 선택하지 않았고, 이미지가 하나 이상 있다면, 첫 번째 이미지를 썸네일로 설정
        this.thumbnailUrl = requestedThumbnailUrl;

        this.images.clear(); // 기존 이미지 목록 제거
        // 이미지 전체 교체 -> 부분 교체로 변경 필요함!
        for (int i = 0; i < newImageUrls.size(); i++) {
            String imageUrl = newImageUrls.get(i);

            PostImage newImage = PostImage.create(
                    imageUrl,
                    i + 1,
                    requestedThumbnailUrl != null
                            && requestedThumbnailUrl.equals(imageUrl) // 대표 이미지 동기화
            );

            this.addImage(newImage); // 편의 메서드로 동기화
        }
    }

    // Post-PostImage 편의 메소드
    public void addImage(PostImage postImage) {
        this.images.add(postImage); // post의 이미지 리스트에 이미지 추가
        postImage.setPost(this); // PostImage에 현재 Post 설정 (양방향 연관관계 동기화)
    }

    // Post-User 양방향 연관 관계 편의 메소드
    public void confirmAuthor(User author) {
        this.author = author;
//        author.getPosts().add(this); // 양방향 관계 동기화
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
