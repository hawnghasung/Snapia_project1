package com.snapia.domain.controller;

import com.snapia.domain.auth.PrincipalDetails;
import com.snapia.domain.dto.RoomDTO;
import com.snapia.domain.entity.Room;
import com.snapia.domain.entity.Users;
import com.snapia.domain.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/room")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    @PostMapping("/create")
    public Room createRoom(@RequestBody Map<String, Long> body, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long receiverId = body.get("receiverId");
        return roomService.createRoom(principalDetails.getUsers(), receiverId);
    }

    @GetMapping("/list")
    public List<RoomDTO> roomList(@AuthenticationPrincipal PrincipalDetails principalDetails) {

        return roomService.roomList(principalDetails.getUsers());
    }
}
