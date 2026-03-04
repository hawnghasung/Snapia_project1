package com.snapia.domain.service;

import com.snapia.domain.dto.CommentDTO;
import com.snapia.domain.entity.Comment;
import com.snapia.domain.entity.Post;
import com.snapia.domain.entity.Users;
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

        Comment comment = builder.build();
        commentRepository.save(comment);
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

    public List<Comment> getCommentsByPost(Long postId) {
        return commentRepository.findByPostPid(postId);
    }

    public List<Comment> getRepliesByComment(Long commentId) {
        return commentRepository.findByReplyCommentCid(commentId);
    }

    public Long getCommentCount(Long postId) {
        return commentRepository.countByPostPid(postId);
    }
}