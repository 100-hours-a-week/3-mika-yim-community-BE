package com.mika.ktdcloud.community.dto.user.request;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserUpdateRequest {
    @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하로 입력해주세요.")
    private String nickname;
    private String profileImageUrl;

    protected UserUpdateRequest() {}

    @Builder
    public UserUpdateRequest(String nickname, String profileImageUrl) {
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }
}
