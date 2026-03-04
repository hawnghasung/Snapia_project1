package com.snapia.domain.repository;

import com.snapia.domain.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByCommentCidAndUsersUid(Long cid, Long uid);
    Long countByCommentCid(Long cid);
    boolean existsByCommentCidAndUsersUid(Long cid, Long uid);
}