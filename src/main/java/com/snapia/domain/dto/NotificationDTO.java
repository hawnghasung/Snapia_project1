package com.snapia.domain.dto;

import com.snapia.domain.constant.NotificationType;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {
    private Long notid;

    private NotificationType notificationType;

    private Long senderId;

    private Long receiverId;

    private Long followListId;
}
