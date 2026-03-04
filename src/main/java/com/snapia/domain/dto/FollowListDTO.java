package com.snapia.domain.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FollowListDTO {
    private Long userId;

    private String snapiaId;

    private String profileImgUrl;

    private Integer followState;

    private Integer equalUserState;
}
