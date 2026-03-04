package com.snapia.domain.repository;

import com.snapia.domain.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostPid(Long pid);
    List<Comment> findByReplyCommentCid(Long cid);
    Long countByPostPid(Long pid);
}