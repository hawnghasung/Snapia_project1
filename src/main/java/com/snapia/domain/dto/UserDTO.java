package com.snapia.domain.dto;

import com.snapia.domain.constant.Gender;
import com.snapia.domain.constant.PubOrNot;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString(exclude = "password")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long uid;

    @NotBlank(message = "비밀번호는 빈칸일 수 없습니다")
    private String password;

    @NotBlank(message = "아이디는 반드시 입력해주세요")
    private String snapiaId;

    @NotBlank(message = "이름은 반드시 입력해주세요")
    private String name;

    @Email(message = "올바른 이메일 형식이 아닙니다")
    @NotBlank(message = "이메일은 반드시 입력해주세요")
    private String email;

    @NotBlank(message = "폰 번호는 반드시 입력해주세요")
    private String phoneNum;

    private String profileImageUrl;

    private boolean isBusiness;

    private Gender gender;

    private PubOrNot pubOrNot;

    private Long businessProfile_id;
}
