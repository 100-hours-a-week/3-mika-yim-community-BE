package com.mika.ktdcloud.community.controller;

import com.mika.ktdcloud.community.dto.auth.request.LoginRequest;
import com.mika.ktdcloud.community.dto.auth.request.TokenReissueRequest;
import com.mika.ktdcloud.community.dto.auth.response.LoginResponse;
import com.mika.ktdcloud.community.service.AuthService;
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

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    // 토큰 재발급
    @PostMapping("/reissue")
    public ResponseEntity<LoginResponse> reissue(@RequestBody @Valid TokenReissueRequest request) {
        LoginResponse response = authService.reissueToken(request);
        return ResponseEntity.ok(response);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody @Valid TokenReissueRequest request) {
        authService.logout(request);
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

}
