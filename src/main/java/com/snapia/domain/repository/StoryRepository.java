package com.snapia.domain.repository;

import com.snapia.domain.entity.Story;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface StoryRepository extends JpaRepository<Story, Long> {
    List<Story> findByUsersUidInAndCreatedAtAfterOrderByCreatedAtDesc(
            List<Long> userIds, LocalDateTime after);


    List<Story> findByUsersUidAndCreatedAtAfterOrderByCreatedAtDesc(
            Long userId, LocalDateTime after);

    List<Story> findByExpiresAtBefore(LocalDateTime dateTime);

    // StoryRepository.java에 추가
    boolean existsByUsersUidAndCreatedAtAfter(Long userId, LocalDateTime createdAt);


}
