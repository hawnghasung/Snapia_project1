package com.snapia.domain.dto;

import lombok.*;

import java.time.LocalDateTime;

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

    // 내가 이미 봤는지 여부 (선택)
    private Boolean isViewed;

    @Builder.Default
    private Boolean isOwner = false;
}