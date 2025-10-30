package com.mika.ktdcloud.community.service;

import com.mika.ktdcloud.community.dto.auth.request.LoginRequest;
import com.mika.ktdcloud.community.entity.User;
import com.mika.ktdcloud.community.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SessionAuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public void login(LoginRequest request, HttpSession session) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일이 일치하지 않습니다."));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        session.setAttribute("userId", user.getId());
        session.setMaxInactiveInterval(30*60);
    }

    public void logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }
}
