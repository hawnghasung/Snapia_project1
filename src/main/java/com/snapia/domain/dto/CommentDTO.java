package com.snapia.domain.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Long cid;

    private String content;

    private Long pid;

    private Long replyCommentId;
}
