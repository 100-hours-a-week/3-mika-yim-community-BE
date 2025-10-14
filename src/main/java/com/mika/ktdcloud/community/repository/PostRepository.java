package com.mika.ktdcloud.community.repository;

import com.mika.ktdcloud.community.entity.Post;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    // fetch join으로 N+1 방지, LEFT JOIN으로 null 값이 있어도 가져옴
    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.images WHERE p.id = :id")
    Optional<Post> findByIdWithImages(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE) // 동시성 문제를 위한 pessimistic lock(비관적 락, 배타적 잠금)
    Optional<Post> findWithLockById(Long id);

    List<Post> findAllByAuthorId(Long authorId);
}
