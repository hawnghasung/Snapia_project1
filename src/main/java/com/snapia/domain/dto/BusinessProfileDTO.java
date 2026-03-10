package com.snapia.domain.dto;

import com.snapia.domain.constant.Category;
import com.snapia.domain.entity.Users;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessProfileDTO {

    private Long bpid;

    // 업체명 / 카테고리 / 소개 / 연락처 / 운영시간 / 공지 메시지
    private String companyName;

    private Category category;

    private String introduce;

    private String contact;

    private Integer operatingHours;

    private String noticeMessage;

    private Long user_id;
}
