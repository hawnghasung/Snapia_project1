package com.snapia.domain.dto;

import com.snapia.domain.constant.BusinessNotificationType;
import com.snapia.domain.entity.Users;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessNotificationResponseDTO {
    private Long bnoid;

    //문의·일정 알림 / 일반 DM 알림 분리

    private Long senderId;

    private Long receiverId;

    private boolean isRead;

    private String senderSnapiaId;

    private String receiverSnapiaId;

    private String senderProfileImgUrl;


    private BusinessNotificationType notificationType;
}
