package com.mika.ktdcloud.community.repository;

import com.mika.ktdcloud.community.entity.Post;
import com.mika.ktdcloud.community.entity.PostLike;
import com.mika.ktdcloud.community.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByPostAndUser(Post post, User user);

    List<PostLike> findAllByUserId(Long userId);
}
