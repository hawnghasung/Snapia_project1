package com.snapia.domain.dto;

import com.snapia.domain.entity.Comment;
import com.snapia.domain.entity.Likes;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfilePostDTO {
    private Long pid;

    private String postImgUrl;

    private String mediaType; // 추가

    private Integer likeCount;

    private Integer commentCount;

    private String thumbnailUrl; // 추가 ← 영상 썸네일 경로
}