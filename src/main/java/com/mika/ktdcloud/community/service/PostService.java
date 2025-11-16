package com.mika.ktdcloud.community.service;

import com.mika.ktdcloud.community.dto.post.request.PostCreateRequest;
import com.mika.ktdcloud.community.dto.post.request.PostUpdateRequest;
import com.mika.ktdcloud.community.dto.post.response.PostDetailResponse;
import com.mika.ktdcloud.community.dto.post.response.PostLikeResponse;
import com.mika.ktdcloud.community.dto.post.response.PostSimpleResponse;
import com.mika.ktdcloud.community.entity.Post;
import com.mika.ktdcloud.community.entity.PostImage;
import com.mika.ktdcloud.community.entity.PostLike;
import com.mika.ktdcloud.community.entity.User;
import com.mika.ktdcloud.community.mapper.PostMapper;
import com.mika.ktdcloud.community.repository.PostLikeRepository;
import com.mika.ktdcloud.community.repository.PostRepository;
import com.mika.ktdcloud.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostViewService postViewService;
    private final PostMapper postMapper;
    private final PostImageService postImageService;

    // 게시글 생성
    @Transactional
    public PostSimpleResponse createPost(PostCreateRequest request, List<MultipartFile> imageFiles, Long authorId) {
        User author = userRepository.getReferenceById(authorId);
        Post newPost = postMapper.toEntity(request, author);
        Post savedPost = postRepository.save(newPost);

        if(imageFiles != null && !imageFiles.isEmpty()) {
            for (int i=0; i<imageFiles.size(); i++){
                PostImage savedImage = postImageService.storeFile(imageFiles.get(i), savedPost, i);
                savedPost.addImage(savedImage);
            }
        }

        if(!savedPost.getImages().isEmpty()) {
            PostImage firstImage = savedPost.getImages().getFirst();
            savedPost.setThumbnail(firstImage);
        }

        return postMapper.toSimpleResponse(savedPost);
    }

    // 게시글 목록 조회 (무한 스크롤링)
    @Transactional(readOnly = true)
    public Slice<PostSimpleResponse> getPostList(Pageable pageable) {
        return postRepository.findPostsWithDetails(pageable);
    }

    //게시글 상세 조회
    @Transactional(readOnly = true)
    public PostDetailResponse getDetailPost(Long id, Long currentUserId) {
        Post post = postRepository.findByIdWithImages(id).
                orElseThrow(() -> new IllegalArgumentException("Post not found."));

        boolean isAuthor = post.getAuthor().getId().equals(currentUserId);

        boolean isLiked = false;
        if (currentUserId != null) {
            isLiked = postLikeRepository.existsByPostIdAndUserIdAndDeletedAtIsNull(id, currentUserId);
        }

        postViewService.increaseViewCount(id);
        return postMapper.toDetailResponse(post, isAuthor, isLiked);
    }

    // 게시글 수정
    @Transactional
    public PostSimpleResponse updatePost(
            PostUpdateRequest request,
            List<MultipartFile> imageFiles,
            Long postId,
            Long currentUserId
    ) throws AccessDeniedException {
        Post post = postRepository.findByIdWithImages(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found."));

        if (!post.getAuthor().getId().equals(currentUserId)) {
            throw new AccessDeniedException("Only author can update post.");
        }

        List<PostImage> newImages = new ArrayList<>();
        if (imageFiles != null && !imageFiles.isEmpty()) {
            for (int i = 0; i < imageFiles.size(); i++) {
                // ImageService가 파일을 저장하고, '영속화되지 않은' PostImage 엔티티를 반환
                PostImage newImage = postImageService.storeFile(imageFiles.get(i), post, i);
                newImages.add(newImage);
            }
        }

        post.update(request, newImages);

        return postMapper.toSimpleResponse(post);
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(Long id, Long currentUserId) throws AccessDeniedException {
        Post post = postRepository.findByIdWithImages(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found."));
        if (!post.getAuthor().getId().equals(currentUserId)) {
            throw new AccessDeniedException("Only the author of this post can delete.");
        }
        post.softDelete();
    }

    // 좋아요 토글
    @Transactional
    public PostLikeResponse togglePostLike(Long postId, Long currentUserId) {
        Post post = postRepository.findWithLockById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found."));

        User user = userRepository.getReferenceById(currentUserId);

        Optional<PostLike> existingLike = postLikeRepository.findByPostAndUser(post, user);

        boolean isLiked;

        if(existingLike.isPresent()) {
            PostLike like = existingLike.get();
            if (like.getDeletedAt() == null) {
                like.softDelete();
                post.getStat().decreaseLikeCount();
                isLiked = false;
            } else {
                like.restore();
                post.getStat().increaseLikeCount();
                isLiked = true;
            }
        } else {
            PostLike newLike = PostLike.create(user, post);
            postLikeRepository.save(newLike);
            post.getStat().increaseLikeCount();
            isLiked = true;
        }

        return postMapper.toLikeResponse(post.getStat().getLikeCount(), isLiked);
    }
}
