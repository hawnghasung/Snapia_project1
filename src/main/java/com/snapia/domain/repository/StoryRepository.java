package com.snapia.domain.repository;

import com.snapia.domain.dto.StoryDTO;
import com.snapia.domain.entity.Story;
import com.snapia.domain.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface StoryRepository extends JpaRepository<Story, Long> {
    List<Story> findByUsersUidInAndCreatedAtAfterOrderByCreatedAtDesc(
            List<Long> userIds, LocalDateTime after);


    List<Story> findByUsersUidAndCreatedAtAfterOrderByCreatedAtDesc(
            Long userId, LocalDateTime after);

    List<Story> findByExpiresAtBeforeAndIsArchivedFalse(LocalDateTime dateTime);

    List<Story> findByUsersUidAndIsArchivedTrue(Long userId);

}
