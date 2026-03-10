package com.snapia.domain.entity;

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
public class Faq extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fqid;

    //비즈니스별 자주 묻는 질문 목록
    private String faq;

    // 추가 필요
    @ManyToOne
    @JoinColumn(name = "business_id")
    private BusinessProfile businessProfile;
}
