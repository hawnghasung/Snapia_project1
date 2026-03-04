package com.snapia.domain.entity;

import com.snapia.domain.constant.Gender;
import com.snapia.domain.constant.PubOrNot;
import com.snapia.domain.constant.Role;
import com.snapia.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "uid")
public class Users extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;

    @Column(name = "snapia_id", unique = true, nullable = false)
    private String snapiaId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "phone_num", nullable = false, unique = true)
    private String phoneNum;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private String bio;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    private PubOrNot pubOrNot;


}