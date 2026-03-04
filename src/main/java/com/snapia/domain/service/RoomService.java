package com.snapia.domain.service;

import com.snapia.domain.constant.ReadStatus;
import com.snapia.domain.dto.RoomDTO;
import com.snapia.domain.entity.Message;
import com.snapia.domain.entity.Room;
import com.snapia.domain.entity.Users;
import com.snapia.domain.repository.MessageReadStatusRepository;
import com.snapia.domain.repository.RoomRepository;
import com.snapia.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class RoomService {
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final MessageService messageService;
    private final MessageReadStatusRepository messageReadStatusRepository;

    public Room createRoom(Users sender, Long receiverId) {
        Users receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        Optional<Room> existRoom = roomRepository.findRoomByUsers(sender, receiver);

        if (existRoom.isPresent()) {
            return existRoom.get();
        } else {
            Room room = Room.builder()
                    .sender(sender)
                    .receiver(receiver)
                    .build();
            return roomRepository.save(room);
        }
    }

    public List<RoomDTO> roomList(Users user) {
        return roomRepository.findRoomsByUser(user).stream()
                .map(room -> {
                    Message lastMsg = messageService.findLastMessage(room.getRoid());
                    int unreadCount = messageReadStatusRepository
                            .countUnreadMessages(room.getRoid(), user, ReadStatus.UNREAD);

                    return RoomDTO.builder()
                            .roid(room.getRoid())
                            .snapiaId(room.getReceiver().getSnapiaId())
                            .profileImageUrl(room.getReceiver().getProfileImageUrl())
                            .lastMessage(lastMsg != null ? lastMsg.getContent() : "")
                            .lastMessageTime(lastMsg != null ? lastMsg.getCreatedAt() : null)
                            .unreadMessageCount(unreadCount)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public Room findRoom(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("방을 찾을 수 없습니다."));
    }
}
