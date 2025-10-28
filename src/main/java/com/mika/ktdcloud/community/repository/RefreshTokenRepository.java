package com.mika.ktdcloud.community.repository;

import com.mika.ktdcloud.community.entity.RefreshToken;
import com.mika.ktdcloud.community.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUser(User user);
    Optional<RefreshToken> findByTokenValueAndRevokedFalse(String tokenValue);
    void deleteByUserId(Long userId);
}
