package com.snapia.domain.dto;

import com.snapia.domain.constant.SuggestStatus;
import com.snapia.domain.entity.Appointment;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuggestDTO {
    private Long suid;

    private Long appointmentId;

    private LocalDateTime suggestTime;

    private SuggestStatus suggestStatus;

    private String rejectReason;
}
