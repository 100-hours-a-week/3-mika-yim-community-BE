package com.mika.ktdcloud.community.dto.auth.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponse {
    private String accessTokenValue;
    private String refreshTokenValue;
    private Long refreshTokenMaxAgeSeconds;
}
