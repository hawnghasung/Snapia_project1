package com.snapia.domain.dto;

import com.snapia.domain.constant.AppointmentStatus;
import com.snapia.domain.constant.SuggestStatus;
import com.snapia.domain.entity.BusinessProfile;
import com.snapia.domain.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentDTO {

    private Long aid;

    //일정 요청 / 수락 / 거절 / 날짜·시간 / 상태값
    private LocalDateTime requestTime;

    private AppointmentStatus appointmentStatus;

    // 추가 필요

    private Long requester_id; // 요청한 사람


    private Long businessProfile_id; // 요청받은 비즈니스

    private String rejectReason;
}

