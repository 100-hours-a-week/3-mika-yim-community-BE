package com.mika.ktdcloud.community.service;

import com.mika.ktdcloud.community.entity.Post;
import com.mika.ktdcloud.community.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostViewService {
    private final PostRepository postRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW) // 새로운 Transaction을 생성
    public void increaseViewCount(Long postId) {
        Post post = postRepository.findWithLockById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found."));
        post.getStat().increaseViewCount();
    }
}
