package com.snapia.domain.service;

import com.snapia.domain.constant.BusinessNotificationType;
import com.snapia.domain.dto.BusinessNotificationResponseDTO;
import com.snapia.domain.entity.BusinessNotification;
import com.snapia.domain.entity.Users;
import com.snapia.domain.repository.BusinessNotificationRepository;
import com.snapia.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class BusinessNotificationService {
    private final BusinessNotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public void send(Long senderUid, Long receiverUid, BusinessNotificationType type) {
        Users sender = userRepository.findById(senderUid)
                .orElseThrow(() -> new IllegalArgumentException("오류"));

        Users receiver = userRepository.findById(receiverUid)
                .orElseThrow(() -> new IllegalArgumentException("오류"));

        BusinessNotification notification = BusinessNotification.builder()
                .notificationType(type)
                .sender(sender)
                .receiver(receiver)
                .build();

        notificationRepository.save(notification);

        BusinessNotificationResponseDTO responseDTO = BusinessNotificationResponseDTO.builder()
                .bnoid(notification.getBnoid())
                .senderId(sender.getUid())
                .senderSnapiaId(sender.getSnapiaId())
                .senderProfileImgUrl(sender.getProfileImageUrl())
                .build();

        simpMessagingTemplate
                .convertAndSend("/s/business-noti/" + receiverUid, responseDTO);
    }
}
