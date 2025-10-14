package com.mika.ktdcloud.community.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    private SecurityUtil() {}

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // SecurityContextHolder에서 현재 인증된 사용자의 ID를 가져옴
        if (authentication == null || authentication.getName() == null
                || "anonymousUser".equals(authentication.getName())) {
            throw new IllegalStateException("인증된 사용자 정보를 찾을 수 없습니다.");
        }

        try{
            return Long.parseLong(authentication.getName());
        } catch (NumberFormatException e) {
            throw new IllegalStateException("인증된 정보에서 사용자 ID를 가져올 수 없습니다.");
        }
    }
}