package com.mika.ktdcloud.community.config;

import com.mika.ktdcloud.community.util.SecurityUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class AuditingConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            try {
                // SecurityContextHolder에서 user_id 가져옴
                Long userId = SecurityUtil.getCurrentUserId();
                return Optional.of(userId.toString());
            }catch (IllegalStateException e) {
                // 인증 정보가 없으면 안전하게 null 반환
                return Optional.empty();
            }
        };
    }
}
