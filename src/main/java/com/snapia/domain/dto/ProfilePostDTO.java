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

    private Integer likeCount;

    private Integer commentCount;

}
