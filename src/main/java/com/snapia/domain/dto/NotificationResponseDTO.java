package com.snapia.domain.dto;

import com.snapia.domain.constant.NotificationType;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponseDTO {
    private Long notid;

    private NotificationType notificationType;

    private Long senderUid;

    private String senderSnapiaId;

    private String senderProfileImageUrl;
}
