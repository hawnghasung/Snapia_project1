package com.snapia.domain.entity;

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
public class Suggest extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long suid;

    @OneToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    private LocalDateTime suggestTime;

    @Enumerated(EnumType.STRING)
    private SuggestStatus suggestStatus;

    private String rejectReason;
}
