package com.mika.ktdcloud.community.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
public class SessionAuthFilter extends OncePerRequestFilter {
    // 필터 제외 경로 목록
    private static final String[] EXCLUDED_PATHS = {
            "/api/v1/users/signup",
            "/api/v1/auth/login",
            "/terms/**",
            "/images/**",
            "/css/**",
            "/js/**"
    };

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return Arrays.stream(EXCLUDED_PATHS).anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain
    ) throws IOException, ServletException {
        HttpSession session = request.getSession(false); // 없으면 생성하지 않고 null 반환

        if (session==null || session.getAttribute("userId") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Long userId = (Long) session.getAttribute("userId");
        request.setAttribute("userId", userId);

        chain.doFilter(request,response);
    }
}