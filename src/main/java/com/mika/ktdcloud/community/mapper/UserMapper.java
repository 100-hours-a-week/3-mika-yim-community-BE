package com.mika.ktdcloud.community.mapper;

import com.mika.ktdcloud.community.dto.user.request.UserCreateRequest;
import com.mika.ktdcloud.community.dto.user.response.UserResponse;
import com.mika.ktdcloud.community.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    @Value("${aws.cloud-front.url}")
    private String cloudFrontUrl;

    public User toEntity(UserCreateRequest request, String profileImageUrl, String encodedPassword) {
        return User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .nickname(request.getNickname())
                .profileImageUrl(profileImageUrl)
                .build();
    }

    public UserResponse toResponse(User user) {
        String imageUrl;

        if (user.getProfileImageUrl().startsWith("http")) {
            imageUrl = user.getProfileImageUrl();
        } else {
            imageUrl = cloudFrontUrl + user.getProfileImageUrl();
        }

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                imageUrl,
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getDeletedAt()
        );
    }
}
