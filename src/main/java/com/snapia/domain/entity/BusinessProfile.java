package com.snapia.domain.entity;

import com.snapia.domain.constant.Category;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bpid;

    // 업체명 / 카테고리 / 소개 / 연락처 / 운영시간 / 공지 메시지
    private String companyName;

    @Enumerated(EnumType.STRING)
    private Category category;

    private String introduce;

    private String contact;

    private Integer operatingHours;

    private String noticeMessage;

    @OneToOne
    @JoinColumn(name = "user_id")
    private Users users;
}
