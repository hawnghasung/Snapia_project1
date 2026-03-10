package com.snapia.domain.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceItemDTO {
    private Long itemid;

    // 서비스·상품 목록 / 가격 / 설명
    private String serviceItem;

    private Integer price;

    private String Description;

    private Long businessProfile_id;
}
