package com.snapia.domain.repository;

import com.snapia.domain.entity.PostHashTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostHashTagRepository extends JpaRepository<PostHashTag, Long> {
    List<PostHashTag> findByPostPid(Long pid);
    List<PostHashTag> findByHashTagHashTag(String tag);
}
