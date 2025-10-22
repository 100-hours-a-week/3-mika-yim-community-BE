package com.mika.ktdcloud.community.service;

import com.mika.ktdcloud.community.dto.auth.request.LoginRequest;
import com.mika.ktdcloud.community.dto.auth.request.TokenReissueRequest;
import com.mika.ktdcloud.community.dto.auth.response.LoginResponse;
import com.mika.ktdcloud.community.entity.RefreshToken;
import com.mika.ktdcloud.community.entity.User;
import com.mika.ktdcloud.community.jwt.JwtTokenProvider;
import com.mika.ktdcloud.community.repository.RefreshTokenRepository;
import com.mika.ktdcloud.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;


    // 로그인
    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email not found."));
        if (user.getDeletedAt() != null) {
            throw new IllegalArgumentException("Deleted User.");
        }
        // matches()가 salt를 고려해 일치 여부를 확인
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Incorrect password.");
        }

        //Access Token과 Refresh Token 생성
        String accessToken = jwtTokenProvider.createAccessToken(user.getId());
        String refreshTokenValue = jwtTokenProvider.createRefreshToken();

        // Refresh Token DB에 저장
        refreshTokenRepository.findByUser(user)
                .ifPresentOrElse(
                        refreshToken -> refreshToken.updateTokenValue(refreshTokenValue), // 이미 있다면 업데이트
                        () -> refreshTokenRepository.save(new RefreshToken(user, refreshTokenValue)) // 없다면 새로 생성
                );

        return new LoginResponse(accessToken, refreshTokenValue);
    }

    // 토큰 재발급
    @Transactional
    public LoginResponse reissueToken(TokenReissueRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        // Refresh 토큰 유효성 검증
        if (!jwtTokenProvider.validateToken(requestRefreshToken)) {
            throw new IllegalArgumentException("Refresh Token is not valid.");
        }

        // DB에 저장된 Refresh Token과 비교
        RefreshToken refreshToken = refreshTokenRepository.findByTokenValue(requestRefreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Refresh Token doesn't exist."));

        // 새로운 Access Token과 Refresh Token을 모두 생성
        User user = refreshToken.getUser();
        String newAccessToken = jwtTokenProvider.createAccessToken(user.getId());
        String newRefreshToken = jwtTokenProvider.createRefreshToken();

        // DB의 Refresh Token 값을 새로운 값으로 업데이트
        refreshToken.updateTokenValue(newRefreshToken);

        return new LoginResponse(newAccessToken, newRefreshToken);
    }

    // 로그아웃
    @Transactional
    public void logout(TokenReissueRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        // DB에서 현재 사용자의 Refresh Token을 찾아서 삭제
        refreshTokenRepository.findByTokenValue(requestRefreshToken)
                .ifPresent(refreshTokenRepository::delete);
    }
}
