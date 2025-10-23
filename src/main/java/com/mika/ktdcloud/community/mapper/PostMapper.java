package com.mika.ktdcloud.community.mapper;

import com.mika.ktdcloud.community.dto.comment.response.CommentResponse;
import com.mika.ktdcloud.community.dto.post.request.PostCreateRequest;
import com.mika.ktdcloud.community.dto.post.response.PostDetailResponse;
import com.mika.ktdcloud.community.dto.post.response.PostLikeResponse;
import com.mika.ktdcloud.community.dto.post.response.PostSimpleResponse;
import com.mika.ktdcloud.community.entity.Post;
import com.mika.ktdcloud.community.entity.PostImage;
import com.mika.ktdcloud.community.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PostMapper {

    public Post toEntity(PostCreateRequest request, User author) {
        return Post.create(request, author);
    }

    public PostLikeResponse toLikeResponse(int likeCount, boolean isLiked) {
        return new PostLikeResponse(likeCount, isLiked);
    }

    public PostSimpleResponse toSimpleResponse(Post post) {
        return PostSimpleResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .thumbnailUrl(post.getThumbnailUrl())
                .authorNickname(post.getAuthor().getNickname())
                .authorProfileImageUrl(post.getAuthor().getProfileImageUrl())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .viewCount(post.getStat().getViewCount())
                .likeCount(post.getStat().getLikeCount())
                .commentCount(post.getStat().getCommentCount())
                .build();
    }

    public PostDetailResponse toDetailResponse(Post post, boolean isLikedByCurrentUser) {
        List<String> imageUrls = post.getImages().stream()
                .map(PostImage::getImageUrl)
                .toList();
        return PostDetailResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .thumbnailUrl(post.getThumbnailUrl())
                .imageUrls(imageUrls)
                .authorNickname(post.getAuthor().getNickname())
                .authorProfileImageUrl(post.getAuthor().getProfileImageUrl())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .deletedAt(post.getDeletedAt())
                .viewCount(post.getStat().getViewCount())
                .likeCount(post.getStat().getLikeCount())
                .commentCount(post.getStat().getCommentCount())
                .isLikedByCurrentUser(isLikedByCurrentUser)
                .build();
    }
}