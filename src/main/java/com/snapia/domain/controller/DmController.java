package com.snapia.domain.controller;

import com.snapia.domain.auth.PrincipalDetails;
import com.snapia.domain.dto.RoomDTO;
import com.snapia.domain.dto.MessageResponseDTO;
import com.snapia.domain.entity.Message;
import com.snapia.domain.repository.MessageReadStatusRepository;
import com.snapia.domain.constant.ReadStatus;
import com.snapia.domain.entity.Room;
import com.snapia.domain.entity.Users;
import com.snapia.domain.service.MessageService;
import com.snapia.domain.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class DmController {
    private final RoomService roomService;
    private final MessageService messageService;
    private final MessageReadStatusRepository messageReadStatusRepository;


    @GetMapping("/dm/list")
    public String dmList(@AuthenticationPrincipal PrincipalDetails principalDetails, Model model) {
        List<RoomDTO> roomList = roomService.roomList(principalDetails.getUsers());

        model.addAttribute("roomList", roomList);

        return "/dm/list";
    }

    @GetMapping("/dm/{roomId}")
    public String dmRoom(@PathVariable Long roomId, @AuthenticationPrincipal PrincipalDetails principalDetails, Model model) {
        List<Message> messages = messageService.searchMessage(roomId);

        // Message -> MessageResponseDTO 변환 (readStatus 포함)
        List<MessageResponseDTO> messageList = messages.stream().map(m -> {
            // 해당 메시지의 readStatus 조회
            boolean isRead = messageReadStatusRepository
                    .findByMessageMessid(m.getMessid())
                    .stream()
                    .anyMatch(rs -> rs.getReadStatus() == ReadStatus.READ);

            return MessageResponseDTO.builder()
                    .messid(m.getMessid())
                    .content(m.getContent())
                    .senderId(m.getUsers().getUid())
                    .dateTime(m.getCreatedAt())
                    .readStatus(isRead ? ReadStatus.READ : ReadStatus.UNREAD)
                    .replyStoryImgUrl(m.getReplyToStory() != null ? m.getReplyToStory().getStoryImgUrl() : null)
                    .replyStorySid(m.getReplyToStory() != null ? m.getReplyToStory().getSid() : null)
                    .build();
        }).collect(java.util.stream.Collectors.toList());

        messageService.markAsRead(roomId, principalDetails.getUsers());

        Room room = roomService.findRoom(roomId);
        Users opponent = room.getSender().getUid().equals(principalDetails.getUsers().getUid())
                ? room.getReceiver()
                : room.getSender();

        model.addAttribute("messageList", messageList);
        model.addAttribute("roomId", roomId);
        model.addAttribute("currentUid", principalDetails.getUsers().getUid());
        model.addAttribute("opponent", opponent);

        return "/dm/room";
    }

}