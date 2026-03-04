package com.snapia.domain.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDTO {
    private Long roid;

    private String snapiaId;

    private String name;

    private String profileImageUrl;

    private String lastMessage;

    private Integer unreadMessageCount;

    private LocalDateTime lastMessageTime;

}
