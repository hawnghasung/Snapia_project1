package com.snapia.domain.controller;

import com.snapia.domain.auth.PrincipalDetails;
import com.snapia.domain.dto.RoomDTO;
import com.snapia.domain.entity.Message;
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

@Controller
@RequiredArgsConstructor
public class DmController {
    private final RoomService roomService;
    private final MessageService messageService;


    @GetMapping("/dm/list")
    public String dmList(@AuthenticationPrincipal PrincipalDetails principalDetails, Model model) {
        List<RoomDTO> roomList = roomService.roomList(principalDetails.getUsers());

        model.addAttribute("roomList", roomList);

        return "/dm/list";
    }

    @GetMapping("/dm/{roomId}")
    public String dmRoom(@PathVariable Long roomId, @AuthenticationPrincipal PrincipalDetails principalDetails, Model model) {
        List<Message> messageList = messageService.searchMessage(roomId);

        messageService.markAsRead(roomId,principalDetails.getUsers());

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
