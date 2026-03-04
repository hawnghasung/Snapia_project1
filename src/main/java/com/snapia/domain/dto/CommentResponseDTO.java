package com.snapia.domain.dto;

import com.snapia.domain.entity.Comment;
import com.snapia.domain.repository.CommentLikeRepository;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class CommentResponseDTO {
    private Long cid;
    private String content;
    private Long pid;
    private Long replyCommentId;
    private String snapiaId;
    private Long likeCount;
    private boolean isLiked;
    private LocalDateTime createdAt;
    private boolean isMine;

    private String profileImageUrl;

}