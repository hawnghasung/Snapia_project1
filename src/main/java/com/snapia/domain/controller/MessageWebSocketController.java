package com.snapia.domain.controller;

import com.snapia.domain.auth.PrincipalDetails;
import com.snapia.domain.dto.MessageRequestDTO;
import com.snapia.domain.dto.MessageResponseDTO;
import com.snapia.domain.entity.Message;
import com.snapia.domain.entity.Room;
import com.snapia.domain.entity.Story;
import com.snapia.domain.entity.Users;
import com.snapia.domain.repository.StoryRepository;
import com.snapia.domain.service.MessageService;
import com.snapia.domain.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final RoomService roomService;
    private final StoryRepository storyRepository;


    @MessageMapping("/dm/send")
    public void sendMessage(MessageRequestDTO dto, SimpMessageHeaderAccessor headerAccessor) {
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) headerAccessor.getUser();
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        Users user = principalDetails.getUsers();

        Room room = roomService.findRoom(dto.getRoid());

        Story story = null;
        if (dto.getStoryId() != null) {
            story = storyRepository.findById(dto.getStoryId())
                    .orElseThrow(() -> new IllegalArgumentException("오류"));
        }


        Message message = Message.builder()
                .room(room)
                .content(dto.getContent())
                .users(user)
                .replyToStory(story)
                .build();

        MessageResponseDTO response = messageService.sendMessage(message, story);
        messagingTemplate.convertAndSend("/s/dm/" + dto.getRoid(), response);


    }
}
