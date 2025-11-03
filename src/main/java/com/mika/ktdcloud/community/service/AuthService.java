package com.mika.ktdcloud.community.service;

import com.mika.ktdcloud.community.dto.auth.request.LoginRequest;
import com.mika.ktdcloud.community.dto.auth.response.LoginResponse;
import com.mika.ktdcloud.community.dto.auth.response.TokenResponse;
import com.mika.ktdcloud.community.entity.RefreshToken;
import com.mika.ktdcloud.community.entity.User;
import com.mika.ktdcloud.community.jwt.JwtProvider;
import com.mika.ktdcloud.community.repository.RefreshTokenRepository;
import com.mika.ktdcloud.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    // 로그인
    @Transactional
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일이 일치하지 않습니다."));

        // matches()가 salt를 고려해 일치 여부를 확인
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 새로운 토큰 발급
        String accessToken = jwtProvider.createAccessToken(user.getId());
        String refreshToken = jwtProvider.createRefreshToken(user.getId());
        long refreshTokenMaxAge = jwtProvider.getRefreshTokenExpiration();
        Instant refreshTokenExpiresAt = Instant.now().plusSeconds(refreshTokenMaxAge);

        // DB에 refresh token 저장
        refreshTokenRepository.save(new RefreshToken(
                user,
                refreshToken,
                refreshTokenExpiresAt,
                false
        ));

        return new TokenResponse(accessToken, refreshToken, refreshTokenMaxAge);
    }

    // 토큰 재발급
    @Transactional
    public LoginResponse refreshTokens(String requestRefreshToken) {
        // DB에서 refresh token이 유효한지 조회
        RefreshToken refreshToken = refreshTokenRepository.findByTokenValue(requestRefreshToken)
                .orElseThrow(() -> new SecurityException("유효하지 않은 리프레시 토큰"));

        if (refreshToken.isRevoked()) {
            throw new SecurityException("비정상적인 토큰 사용이 감지되었습니다.");
        }

        if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new SecurityException("시간 만료로 로그아웃 되었습니다.");
        }

        User user = refreshToken.getUser();

        // access token 새로 발급
        String newAccessToken = jwtProvider.createAccessToken(user.getId());

        return new LoginResponse(newAccessToken);
    }

    // 로그아웃
    @Transactional
    public void logout(String requestRefreshToken) {
        refreshTokenRepository.findByTokenValue(requestRefreshToken)
                .ifPresent(RefreshToken::revoke);
    }
}
