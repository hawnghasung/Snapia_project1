package com.snapia.domain.repository;

import com.snapia.domain.entity.Post;
import com.snapia.domain.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUsersUid(Long uid);
    // PostRepository에 추가 필요
    List<Post> findByUsersUidInOrderByCreatedAtDesc(List<Long> userIds);
}
