package com.snapia.domain.repository;

import com.snapia.domain.entity.Users;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    @Query("SELECT u FROM Users u WHERE u.email = :loginId OR u.phoneNum = :loginId OR u.snapiaId = :loginId")
    Optional<Users> findByLoginIdentifier(@Param("loginId") String loginId);

    boolean existsBySnapiaId(String snapiaId);
    List<Users> findTop10ByUidNotInOrderByUidDesc(List<Long> excludeIds);

    Optional<Object> findBySnapiaId(String snapiaId);

    List<Users> findBySnapiaIdContainingOrNameContaining(
            String snapiaId, String name, Pageable pageable);
}
