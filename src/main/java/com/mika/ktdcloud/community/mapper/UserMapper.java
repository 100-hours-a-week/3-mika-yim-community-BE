package com.mika.ktdcloud.community.mapper;

import com.mika.ktdcloud.community.dto.user.request.UserCreateRequest;
import com.mika.ktdcloud.community.dto.user.response.UserResponse;
import com.mika.ktdcloud.community.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserCreateRequest request, String encodedPassword) {
        return User.create(
                request.getEmail(),
                encodedPassword,
                request.getNickname(),
                request.getProfileImageUrl()
        );
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getProfileImageUrl(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getDeletedAt()
        );
    }
}
