package com.mika.ktdcloud.community.controller;

import com.mika.ktdcloud.community.dto.auth.request.LoginRequest;
import com.mika.ktdcloud.community.dto.auth.response.LoginResponse;
import com.mika.ktdcloud.community.dto.auth.response.TokenResponse;
import com.mika.ktdcloud.community.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody @Valid LoginRequest request,
            HttpServletResponse httpServletResponse
    ) {
        TokenResponse tokenResponse = authService.login(request);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", tokenResponse.getRefreshTokenValue())
                .httpOnly(true)
                .path("/api/v1/auth/refresh")
                .maxAge(tokenResponse.getRefreshTokenMaxAgeSeconds())
                .build();

        httpServletResponse.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok(new LoginResponse(tokenResponse.getAccessTokenValue()));
    }

    // 토큰 재발급
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(
            @CookieValue(name = "refreshToken", required = true) String refreshToken,
            HttpServletResponse httpServletResponse
    ) {
        LoginResponse loginResponse = authService.refreshTokens(refreshToken);
        return ResponseEntity.ok(loginResponse);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse httpServletResponse
    ) {
        if (refreshToken!=null) {
            authService.logout(refreshToken); // DB refresh token 만료
        }
        // 쿠키 만료
        ResponseCookie cookie = ResponseCookie.from("refreshToken", null)
                .httpOnly(true)
                .path("/api/v1/auth/refresh")
                .maxAge(0)
                .build();

        httpServletResponse.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok("로그아웃 되었습니다.");
    }
}