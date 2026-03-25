package com.snapia.domain.dto;

import com.snapia.domain.constant.AppointmentStatus;
import com.snapia.domain.constant.SuggestStatus;
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

    private LocalDateTime requestTime;

    private AppointmentStatus appointmentStatus;

    private LocalDateTime suggestTime;
    private Long suid;
    private SuggestStatus suggestStatus;

    private Long requester_id;
    private Long businessProfile_id;


    private String requesterSnapiaId;  // 추가
    private String requesterName;

    private String rejectReason;

    private Long cancelUid; // 취소 요청자 uid
}