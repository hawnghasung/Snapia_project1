package com.snapia.domain.entity;

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
public class Inquiry extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inid;

    //협찬·제안 문의함 / 읽음 여부 / DM방 연결
    private String sponsorship;

    @ManyToOne
    @JoinColumn(name = "read_id")
    private MessageReadStatus isRead;

    @ManyToOne
    @JoinColumn(name = "message_id")
    private Message message;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

}
