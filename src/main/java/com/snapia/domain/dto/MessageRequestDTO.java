package com.snapia.domain.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequestDTO {
    private Long roid;

    private String content;

}
