package com.mika.ktdcloud.community.repository;

import com.mika.ktdcloud.community.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    void deleteByUser_Id(Long userId);
    Optional<RefreshToken> findByTokenValue(String tokenValue);
}
