package com.snapia.domain.service;

import com.snapia.domain.dto.CommentDTO;
import com.snapia.domain.dto.CommentResponseDTO;
import com.snapia.domain.entity.Comment;
import com.snapia.domain.entity.Post;
import com.snapia.domain.entity.Users;
import com.snapia.domain.repository.CommentLikeRepository;
import com.snapia.domain.repository.CommentRepository;
import com.snapia.domain.repository.PostRepository;
import com.snapia.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentLikeRepository commentLikeRepository;

    public void register(CommentDTO commentDTO, String snapiaId) {
        Users users = userRepository.findByLoginIdentifier(snapiaId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 조회 불가"));

        Post post = postRepository.findById(commentDTO.getPid())
                .orElseThrow(() -> new IllegalArgumentException("게시물 조회 불가"));

        Comment.CommentBuilder builder = Comment.builder()
                .content(commentDTO.getContent())
                .users(users)
                .post(post);

        if (commentDTO.getReplyCommentId() != null) {
            Comment replyComment = commentRepository.findById(commentDTO.getReplyCommentId())
                    .orElseThrow(() -> new IllegalArgumentException("원댓글 조회 불가"));
            builder.replyComment(replyComment);
        }

        commentRepository.save(builder.build());
    }

    public void update(Long cid, String content, String snapiaId) {
        Comment comment = commentRepository.findById(cid)
                .orElseThrow(() -> new IllegalArgumentException("댓글 조회 불가"));

        if (!comment.getUsers().getSnapiaId().equals(snapiaId)) {
            throw new IllegalArgumentException("본인 댓글만 수정 가능합니다");
        }

        comment.setContent(content);
    }

    public void delete(Long cid, String snapiaId) {
        Comment comment = commentRepository.findById(cid)
                .orElseThrow(() -> new IllegalArgumentException("댓글 조회 불가"));

        if (!comment.getUsers().getSnapiaId().equals(snapiaId)) {
            throw new IllegalArgumentException("본인 댓글만 삭제 가능합니다");
        }

        commentRepository.delete(comment);
    }

    public List<CommentResponseDTO> getCommentsByPost(Long postId, String snapiaId) {
        Users users = userRepository.findByLoginIdentifier(snapiaId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 조회 불가"));

        return commentRepository.findByPostPid(postId).stream()
                .map(comment -> toResponseDTO(comment, users.getUid(), snapiaId))
                .toList();
    }

    public List<CommentResponseDTO> getRepliesByComment(Long commentId, String snapiaId) {
        Users users = userRepository.findByLoginIdentifier(snapiaId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 조회 불가"));

        return commentRepository.findByReplyCommentCid(commentId).stream()
                .map(comment -> toResponseDTO(comment, users.getUid(), snapiaId))
                .toList();
    }

    public Long getCommentCount(Long postId) {
        return commentRepository.countByPostPid(postId);
    }

    private CommentResponseDTO toResponseDTO(Comment comment, Long uid, String snapiaId) {
        return CommentResponseDTO.builder()
                .cid(comment.getCid())
                .content(comment.getContent())
                .pid(comment.getPost().getPid())
                .replyCommentId(comment.getReplyComment() != null ? comment.getReplyComment().getCid() : null)
                .snapiaId(comment.getUsers().getSnapiaId())
                .likeCount(commentLikeRepository.countByCommentCid(comment.getCid()))
                .isLiked(commentLikeRepository.existsByCommentCidAndUsersUid(comment.getCid(), uid))
                .createdAt(comment.getCreatedAt())
                .isMine(comment.getUsers().getSnapiaId().equals(snapiaId))
                .profileImageUrl(comment.getUsers().getProfileImageUrl())
                .build();
    }
}