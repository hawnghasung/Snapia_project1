package com.snapia.domain.entity;

import com.snapia.domain.constant.FollowStatus;
import com.snapia.domain.constant.NotificationType;
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
public class Notification extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notid;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @JoinColumn(name = "follow_list")
    @ManyToOne
    private FollowList followList;

    @ManyToOne
    @JoinColumn(name = "sender")
    private Users sender;

    @ManyToOne
    @JoinColumn(name = "receiver")
    private Users receiver;

    private boolean isRead;

}
