package com.mika.ktdcloud.community.controller;

import com.mika.ktdcloud.community.dto.auth.request.LoginRequest;
import com.mika.ktdcloud.community.dto.auth.request.TokenRefreshRequest;
import com.mika.ktdcloud.community.dto.auth.response.LoginResponse;
import com.mika.ktdcloud.community.service.AuthService;
import com.mika.ktdcloud.community.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody @Valid LoginRequest request,
            HttpServletResponse httpServletResponse) {
        LoginResponse loginResponse = authService.login(request);
        cookieUtil.addTokenCookies(httpServletResponse, loginResponse);
        return ResponseEntity.ok(loginResponse);
    }

    // 토큰 재발급
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> reissue(
            @RequestBody @Valid TokenRefreshRequest request,
            HttpServletResponse httpServletResponse) {
        LoginResponse loginResponse = authService.refreshTokens(request);
        cookieUtil.addTokenCookies(httpServletResponse, loginResponse);
        return ResponseEntity.ok(loginResponse);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestBody @Valid TokenRefreshRequest request,
            HttpServletResponse httpServletResponse) {
        authService.logout(request);
        cookieUtil.deleteTokenCookies(httpServletResponse);
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }
}
