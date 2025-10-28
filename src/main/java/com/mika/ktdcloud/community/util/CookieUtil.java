package com.mika.ktdcloud.community.util;


import com.mika.ktdcloud.community.dto.auth.response.LoginResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    private final int accessTokenExpiration;
    private final int refreshTokenExpiration;

    public CookieUtil(
            @Value("${jwt.access-token-expiration}") int accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}") int refreshTokenExpiration) {
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public void addTokenCookies(HttpServletResponse response, LoginResponse loginResponse) {
        addTokenCookie(response, "accessToken", loginResponse.getAccessToken(), accessTokenExpiration);
        addTokenCookie(response, "refreshToken", loginResponse.getRefreshToken(), refreshTokenExpiration);
    }

    // 쿠키 생성
    public void addTokenCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    public void deleteTokenCookies(HttpServletResponse response) {
        deleteTokenCookie(response, "accessToken");
        deleteTokenCookie(response, "refreshToken");
    }

    // 쿠키 삭제(즉시 만료)
    public void deleteTokenCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null); // 값을 null로 설정
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 유효 기간을 0으로 설정하여 즉시 만료
        response.addCookie(cookie);
    }
}
