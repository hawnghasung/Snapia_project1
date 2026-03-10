package com.snapia.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostHashTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long phid;

    @ManyToOne
    @JoinColumn(name = "hashtag_id")
    private HashTag hashTag;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
}
