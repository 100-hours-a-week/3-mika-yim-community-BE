package com.mika.ktdcloud.community.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // application.yml에서 jwt 설정값 가져오기
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.access-token-expiration-ms}")
    private long accessTokenExpiration;
    @Value("${jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpiration;

    private SecretKey secretKey;

    // secret-key 값 설정
    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Access Token 생성
    public String createAccessToken(Long userId) {
        Date now = new Date();
        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + accessTokenExpiration))
                .signWith(secretKey)
                .compact();
    }

    // Refresh Token 생성
    public String createRefreshToken() {
        Date now = new Date();
        return Jwts.builder()
                .issuedAt(now)
                .expiration(new Date(now.getTime() + refreshTokenExpiration))
                .signWith(secretKey)
                .compact();
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // JWT 토큰을 복호화해 인증 정보 조회
    public Authentication getAuthentication (String accessToken) {
        Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(accessToken).getPayload();
        // UserDetails 객체를 만들어서 Authentication 반환
        UserDetails userDetails = new User(claims.getSubject(), "", Collections.emptyList());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 사용자 ID 추출
    public Long getUserIdFromToken(String token) {
        String subject = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getSubject();
        return Long.parseLong(subject);
    }
}
