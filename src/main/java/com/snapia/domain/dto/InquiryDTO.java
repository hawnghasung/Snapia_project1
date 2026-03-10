package com.snapia.domain.dto;

import com.snapia.domain.constant.ReadStatus;
import com.snapia.domain.entity.Message;
import com.snapia.domain.entity.MessageReadStatus;
import com.snapia.domain.entity.Room;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InquiryDTO {
    private Long inid;

    //협찬·제안 문의함 / 읽음 여부 / DM방 연결
    private String sponsorship;


    private ReadStatus isRead;


    private Long message_id;


    private Long room_id;
}
