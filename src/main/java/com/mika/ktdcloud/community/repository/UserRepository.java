package com.mika.ktdcloud.community.repository;

import com.mika.ktdcloud.community.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> { // 상속받을 엔티티와 PK 타입을 인수로 넣는다.
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email); // 이메일 중복 체크
    boolean existsByNickname(String nickname); // 닉네임 중복 체크
    Optional<User> findByIdAndDeletedAtIsNull(Long Id);
}
