package com.snapia.domain.entity;

import com.snapia.domain.constant.FollowStatus;
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
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"from_user_id", "to_user_id"})
})
public class FollowList extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fid;

    @ManyToOne
    @JoinColumn(name = "from_user_id")
    private Users fromUser; //팔로우 거는 사람

    @ManyToOne
    @JoinColumn(name = "to_user_id")
    private Users toUser; //팔로우 받는 사람

    @Enumerated(EnumType.STRING)
    private FollowStatus followStatus;

}
