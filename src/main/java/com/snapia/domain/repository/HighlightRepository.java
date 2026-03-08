package com.snapia.domain.repository;

import com.snapia.domain.entity.Highlight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HighlightRepository extends JpaRepository<Highlight, Long> {
    // 유저의 하이라이트 목록 조회
    List<Highlight> findByUsersUid(Long uid);

    // 하이라이트 단건 조회 (본인 것만)
    Optional<Highlight> findByHidAndUsersUid(Long hid, Long uid);
}
