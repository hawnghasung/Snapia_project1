package com.snapia.domain.repository;

import com.snapia.domain.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Long> {
    Optional<Likes> findByPostPidAndUsersUid(Long pid, Long uid);
    Long countByPostPid(Long pid);
    boolean existsByPostPidAndUsersUid(Long pid, Long uid);
}