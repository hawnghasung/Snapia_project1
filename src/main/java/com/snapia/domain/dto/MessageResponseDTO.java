package com.snapia.domain.dto;

import com.snapia.domain.constant.ReadStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponseDTO {
    private Long senderId;

    private Long messid;

    private Long roid;

    private String content;

    private LocalDateTime dateTime;

    private ReadStatus readStatus;
}
