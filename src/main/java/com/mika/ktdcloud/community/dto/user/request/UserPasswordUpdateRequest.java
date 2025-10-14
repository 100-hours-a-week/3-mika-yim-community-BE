package com.mika.ktdcloud.community.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserPasswordUpdateRequest {
    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    private String currentPassword;
    @NotBlank(message = "새로운 비밀번호를 입력해주세요.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상, 20자 이하여야 합니다.")
    private String newPassword;
    @NotBlank(message = "비밀번호 확인을 입력해주세요.")
    private String newPasswordConfirm;

    protected UserPasswordUpdateRequest () {}

    public UserPasswordUpdateRequest(String currentPassword, String newPassword, String newPasswordConfirm) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.newPasswordConfirm = newPasswordConfirm;
    }

}
