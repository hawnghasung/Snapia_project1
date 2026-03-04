package com.snapia.domain.entity;

import com.snapia.domain.constant.PubOrNot;
import com.snapia.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pid;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users users;

    /* @Builder.Default
    private Integer likes = 0;
    별도의 entity 로 분리해야함
    private String reply; */

    @Enumerated(EnumType.STRING)
    private PubOrNot pubOrNot;

    private String postImgUrl;

}
