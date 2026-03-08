package com.snapia.domain.service;

import com.snapia.domain.constant.ReadStatus;
import com.snapia.domain.dto.MessageResponseDTO;
import com.snapia.domain.entity.*;
import com.snapia.domain.repository.MessageReadStatusRepository;
import com.snapia.domain.repository.MessageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class MessageService {
    private final MessageRepository messageRepository;
    private final MessageReadStatusRepository messageReadStatusRepository;

    public MessageResponseDTO sendMessage(Message message, Story story) {
        Message saveMessage = messageRepository.save(message);

        Users opponent = message.getUsers().equals(message.getRoom().getSender())
                ? message.getRoom().getReceiver()
                : message.getRoom().getSender();

        MessageReadStatus readStatus = MessageReadStatus.builder()
                .message(saveMessage)
                .readStatus(ReadStatus.UNREAD)
                .users(opponent)
                .build();

        messageReadStatusRepository.save(readStatus);


        MessageResponseDTO messageResponseDTO = MessageResponseDTO.builder()
                .readStatus(ReadStatus.UNREAD)
                .messid(saveMessage.getMessid())
                .content(saveMessage.getContent())
                .senderId(saveMessage.getUsers().getUid())
                .dateTime(saveMessage.getCreatedAt())
                .replyStorySid(story != null ? story.getSid() : null)
                .replyStoryImgUrl(story != null ? story.getStoryImgUrl() : null)
                .build();

        return messageResponseDTO;
    }

    public List<Message> searchMessage(Long roomId) {
        return messageRepository.findMessagesByRoomId(roomId);
    }

    public void markAsRead(Long roomId, Users user) {
        List<MessageReadStatus> unreadList = messageReadStatusRepository
                .findByRoomAndUserAndStatus(roomId, user, ReadStatus.UNREAD);

        for (MessageReadStatus status : unreadList) {
            status.setReadStatus(ReadStatus.READ);
        }
    }

    public Message findLastMessage(Long roomId) {
        Message message = messageRepository.findLastMessageByRoomId(roomId)
                .orElse(null);

        return message;
    }
}
