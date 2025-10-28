package com.mika.ktdcloud.community.service;

import com.mika.ktdcloud.community.dto.auth.request.LoginRequest;
import com.mika.ktdcloud.community.dto.auth.request.TokenRefreshRequest;
import com.mika.ktdcloud.community.dto.auth.response.LoginResponse;
import com.mika.ktdcloud.community.entity.RefreshToken;
import com.mika.ktdcloud.community.entity.User;
import com.mika.ktdcloud.community.jwt.JwtProvider;
import com.mika.ktdcloud.community.repository.RefreshTokenRepository;
import com.mika.ktdcloud.community.repository.UserRepository;
import com.mika.ktdcloud.community.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
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
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일이 일치하지 않습니다."));

        // matches()가 salt를 고려해 일치 여부를 확인
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 새로운 토큰 발급 및 저장
        String accessToken = jwtProvider.createAccessToken(user.getId());
        String refreshToken = jwtProvider.createRefreshToken(user.getId());
        Instant refreshTokenExpiresAt = Instant.now().plusMillis(jwtProvider.getRefreshTokenExpiration());

        // 사용자의 리프레쉬 토큰이 이미 DB에 있다면 업데이트, 없다면 새로 생성
        refreshTokenRepository.findByUser(user)
                .ifPresentOrElse(
                        token -> token.updateToken(refreshToken, refreshTokenExpiresAt),
                        () -> refreshTokenRepository.save(new RefreshToken(
                                user,
                                refreshToken,
                                refreshTokenExpiresAt,
                                false
                        ))
                );

        return new LoginResponse(accessToken, refreshToken);
    }

    // 토큰 재발급
    @Transactional
    public LoginResponse refreshTokens(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        // DB에서 refresh token이 유효한지 조회
        RefreshToken refreshToken = refreshTokenRepository.findByTokenValueAndRevokedFalse(requestRefreshToken).orElse(null);

        if (refreshToken == null || refreshToken.getExpiresAt().isBefore(Instant.now())) {
            return null;
        }

        User user = refreshToken.getUser();

        // 보안을 위해 access token과 refresh token 모두 새로 발급
        String newAccessToken = jwtProvider.createAccessToken(user.getId());
        String newRefreshToken = jwtProvider.createRefreshToken(user.getId());
        Instant refreshTokenExpiresAt = Instant.now().plusMillis(jwtProvider.getRefreshTokenExpiration());

        // DB의 기존 토큰에 업데이트
        refreshToken.updateToken(newRefreshToken, refreshTokenExpiresAt);

        return new LoginResponse(newAccessToken, newRefreshToken);
    }

    // 로그아웃
    @Transactional
    public void logout(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        // DB에서 토큰을 찾아 revoke 상태로 변경
        refreshTokenRepository.findByTokenValueAndRevokedFalse(requestRefreshToken)
                .ifPresent(RefreshToken::revoke);

    }
}
