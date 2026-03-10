package com.snapia.domain.repository;

import com.snapia.domain.entity.HashTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HashTagRepository extends JpaRepository<HashTag, Long> {
    // 게시물 작성할 때 이미 존재하는 태그인지 체크해서 있으면 재사용, 없으면 새로 생성할 때 사용
    Optional<HashTag> findByHashTag(String name);

    // 검색창에 #존맛 입력하면 자동완성 목록 뽑을 때 사용
    List<HashTag> findByHashTagContaining(String name);
}
