package com.snapia.domain.entity;

import com.snapia.domain.constant.BusinessNotificationType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bnoid;

    //문의·일정 알림 / 일반 DM 알림 분리
    @ManyToOne
    @JoinColumn(name = "sender")
    private Users sender;

    @ManyToOne
    @JoinColumn(name = "receiver")
    private Users receiver;

    private boolean isRead;

    // 추가 필요
    @Enumerated(EnumType.STRING)
    private BusinessNotificationType notificationType;
}
