package com.snapia.domain.entity;

import com.snapia.domain.entity.Users;
import com.snapia.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Story extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sid;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users users;

    private String storyImgUrl;

    // 24시간 후 자동 삭제용
    private LocalDateTime expiresAt;

    private boolean isArchived = false;

    //조회한 유저 목록
    @ManyToMany
    @JoinTable(
            name = "story_view_users",
            joinColumns = @JoinColumn(name = "story_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<Users> viewUser;

    private String mediaType;     // "IMAGE" or "VIDEO"
    private String thumbnailUrl;  // 동영상 썸네일
    private Integer duration;     // 동영상 길이 (초)

}