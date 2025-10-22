package com.mika.ktdcloud.community.config;

import com.mika.ktdcloud.community.dto.post.request.PostCreateRequest;
import com.mika.ktdcloud.community.entity.Post;
import com.mika.ktdcloud.community.entity.User;
import com.mika.ktdcloud.community.repository.PostRepository;
import com.mika.ktdcloud.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.IntStream;

@Configuration
@Profile("dev")
@RequiredArgsConstructor
public class SeedConfig {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner seedRunner() {
        return args -> seed(); // 부트 기동 후 1회 실행
    }

    @Transactional
    void seed() {
        if (userRepository.count() >= 10 && postRepository.count() >= 10) return;

        IntStream.rangeClosed(1, 100).forEach(i -> {
            String encodedPassword = passwordEncoder.encode("Pass1234!"+i);
            User user = User.create("tester"+i+"@example.kr", encodedPassword, "tester"+i, null);
            userRepository.save(user);

            PostCreateRequest postCreateRequest = PostCreateRequest.builder()
                    .title("title" + i)
                    .content("content" + i)
                    .build();

            Post post = Post.create(postCreateRequest, user);
            postRepository.save(post);
        });
    }
}
