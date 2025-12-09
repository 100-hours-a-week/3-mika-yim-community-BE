package com.mika.ktdcloud.community.repository;

import com.mika.ktdcloud.community.entity.PostStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostStatRepository extends JpaRepository<PostStat, Long> {
}