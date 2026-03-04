package com.snapia.domain.entity;

import com.snapia.domain.entity.Users;
import com.snapia.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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
}