package com.snapia.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Highlight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hid;

    private String title;

    @ManyToMany
    private List<Story> content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users users;

    private String coverImgUrl;
}
