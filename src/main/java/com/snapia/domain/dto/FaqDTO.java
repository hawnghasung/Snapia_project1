package com.snapia.domain.dto;

import com.snapia.domain.entity.BusinessProfile;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FaqDTO {
    private Long fqid;

    //비즈니스별 자주 묻는 질문 목록
    private String faq;

    // 추가 필요

    private Long businessProfile_id;
}
