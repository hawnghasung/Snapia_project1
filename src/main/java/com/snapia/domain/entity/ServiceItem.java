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
public class ServiceItem extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemid;

    // 서비스·상품 목록 / 가격 / 설명
    private String serviceItem;

    private Integer price;

    private String Description;

    // 어느 비즈니스의 서비스인지 모름
    @ManyToOne
    @JoinColumn(name = "business_id")
    private BusinessProfile businessProfile; // 추가 필요


}
