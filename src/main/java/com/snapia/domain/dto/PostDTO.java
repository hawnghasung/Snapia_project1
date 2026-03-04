package com.snapia.domain.dto;

import com.snapia.domain.constant.PubOrNot;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDTO {
    private Long pid;
    private String content;
    private MultipartFile multipartFile;
    private String postImgUrl;
    private PubOrNot pubOrNot;

    // 뷰 표시용 추가 필드
    private Long userUid;
    private String userSnapiaId;
    private String userProfileImageUrl;
    private Integer likeCount;
    private Integer commentCount;
}