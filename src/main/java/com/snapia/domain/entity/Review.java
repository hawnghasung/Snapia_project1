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
public class Review extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rvid;

    //일정 완료 후 별점·후기 / 비즈니스에 연결
    private Integer rating;

    @ManyToOne
    @JoinColumn(name = "business_id")
    private BusinessProfile businessProfile;
}
