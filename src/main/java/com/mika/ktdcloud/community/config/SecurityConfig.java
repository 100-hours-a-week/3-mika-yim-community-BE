package com.mika.ktdcloud.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    // 이 메서드가 반환하는 객체를 스프링 빈으로 등록
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
        // BCrypt 해싱 알고리즘으로 비밀번호 암호화
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                // CSRF 보호 비활성화
                // 요즘 API 서버에서는 세션이 아닌 JWT를 많이 사용하기 때문에 비활성화

                // HTTP 요청에 대한 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/users/signup",
                                "/api/v1/users/login",
                                "/api/v1/users/**", // (주의) 나중에 삭제해야 됨!
                                "/api/v1/posts/**" // (주의) 나중에 삭제해야 됨!
                        ).permitAll()
                        // 위 경로는 인증 없이 누구나 접근 허용
                        .anyRequest().authenticated()
                        // 그 외의 모든 요청은 반드시 인증을 거쳐야 함
                );

        return http.build();
    }

}
