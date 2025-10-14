package com.mika.ktdcloud.community.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class TokenReissueRequest {

    @NotBlank(message = "리프레시 토큰이 필요합니다.")
    private final String refreshToken;

    public TokenReissueRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
