package com.snapia.domain.controller;

import com.snapia.domain.auth.PrincipalDetails;
import com.snapia.domain.dto.MessageResponseDTO;
import com.snapia.domain.entity.Message;
import com.snapia.domain.entity.Users;
import com.snapia.domain.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/mess")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @GetMapping("/smess")
    public List<MessageResponseDTO> messageList(@RequestParam Long roomId) {
        return messageService.searchMessage(roomId).stream()
                .map(m -> MessageResponseDTO.builder()
                        .messid(m.getMessid())
                        .content(m.getContent())
                        .dateTime(m.getCreatedAt())
                        .senderId(m.getUsers().getUid())
                        .roid(m.getRoom().getRoid())
                        .build())
                .collect(Collectors.toList());
    }

    @PostMapping("/read")
    public void markAsRead(@RequestParam Long roomId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        messageService.markAsRead(roomId, principalDetails.getUsers());
    }



}
