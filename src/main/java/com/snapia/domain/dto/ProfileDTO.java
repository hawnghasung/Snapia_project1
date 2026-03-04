package com.snapia.domain.dto;

import com.snapia.domain.constant.Gender;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileDTO {
    private String bio;

    private String snapiaId;

    private Gender gender;

    private MultipartFile multipartFile;

    private String profileImageUrl;

    private String name;

    private String password;

    private String email;
}
