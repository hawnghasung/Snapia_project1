package com.snapia.domain.entity;

import com.snapia.domain.constant.AppointmentStatus;
import com.snapia.domain.constant.SuggestStatus;
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
public class Appointment extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long aid;

    //일정 요청 / 수락 / 거절 / 날짜·시간 / 상태값


    private LocalDateTime requestTime;


    @Enumerated(EnumType.STRING)
    private AppointmentStatus appointmentStatus;

    // 추가 필요
    @ManyToOne
    @JoinColumn(name = "requester_id")
    private Users requester; // 요청한 사람

    @ManyToOne
    @JoinColumn(name = "business_id")
    private BusinessProfile businessProfile; // 요청받은 비즈니스

    private String rejectReason;

}
