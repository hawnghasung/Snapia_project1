package com.snapia.domain.service;

import com.snapia.domain.entity.Comment;
import com.snapia.domain.entity.CommentLike;
import com.snapia.domain.entity.Users;
import com.snapia.domain.repository.CommentLikeRepository;
import com.snapia.domain.repository.CommentRepository;
import com.snapia.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class CommentLikeService {
    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public void toggleLike(Long commentId, String snapiaId) {
        Users users = userRepository.findByLoginIdentifier(snapiaId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 조회 불가"));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 조회 불가"));

        commentLikeRepository.findByCommentCidAndUsersUid(commentId, users.getUid())
                .ifPresentOrElse(
                        commentLikeRepository::delete,
                        () -> {
                            CommentLike commentLike = CommentLike.builder()
                                    .comment(comment)
                                    .users(users)
                                    .build();
                            commentLikeRepository.save(commentLike);
                        }
                );
    }

    public Long getLikeCount(Long commentId) {
        return commentLikeRepository.countByCommentCid(commentId);
    }

    public boolean isLikedByUser(Long commentId, String snapiaId) {
        Users users = userRepository.findByLoginIdentifier(snapiaId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 조회 불가"));

        return commentLikeRepository.existsByCommentCidAndUsersUid(commentId, users.getUid());
    }
}