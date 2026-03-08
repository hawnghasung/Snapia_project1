package com.snapia.domain.dto;

import com.snapia.domain.entity.Users;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoryDTO {
    private Long sid;

    private Long userId;

    private String snapiaId;

    private String profileImageUrl;

    private String storyImgUrl;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    private List<Users> viewUser;

    // 내가 이미 봤는지 여부 (선택)
    private Boolean isViewed;

    @Builder.Default
    private Boolean isOwner = false;

    private MultipartFile multipartFile;  // 파일 업로드용 (import 필요)
    private String mediaType;
    private String thumbnailUrl;
    private Integer duration;
}