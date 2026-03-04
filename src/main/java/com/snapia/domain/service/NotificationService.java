package com.snapia.domain.service;

import com.snapia.domain.dto.FollowListDTO;
import com.snapia.domain.dto.NotificationDTO;
import com.snapia.domain.dto.NotificationResponseDTO;
import com.snapia.domain.entity.FollowList;
import com.snapia.domain.entity.Notification;
import com.snapia.domain.entity.Users;
import com.snapia.domain.repository.FollowListRepository;
import com.snapia.domain.repository.NotificationRepository;
import com.snapia.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final FollowListRepository followListRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public void saveNotification(NotificationDTO notificationDTO) {
        Users sender = userRepository.findById(notificationDTO.getSenderId())
                .orElseThrow(()->new IllegalArgumentException("오류"));
        Users receiver = userRepository.findById(notificationDTO.getReceiverId())
                .orElseThrow(() -> new IllegalArgumentException("오류"));
        FollowList followList = followListRepository.findById(notificationDTO.getFollowListId())
                .orElseThrow(()->new IllegalArgumentException("오류"));

        Notification notification = Notification.builder()
                .sender(sender)
                .receiver(receiver)
                .followList(followList)
                .notificationType(notificationDTO.getNotificationType())
                .build();

        notificationRepository.save(notification);

        NotificationResponseDTO notificationResponseDTO = NotificationResponseDTO.builder()
                .notid(notification.getNotid())
                .notificationType(notification.getNotificationType())
                .senderSnapiaId(notification.getSender().getSnapiaId())
                .senderProfileImageUrl(notification.getSender().getProfileImageUrl())
                .senderUid(notification.getSender().getUid())
                .build();


        simpMessagingTemplate
                .convertAndSend("/s/noti/" + notificationDTO.getReceiverId()
                        ,notificationResponseDTO);
    }

    public void delNotification(Long followListId) {
        notificationRepository.deleteByFollowListFid(followListId);
    }


    public List<NotificationResponseDTO> NotificationSearch(Long receiverUid) {
        // 1. 기준 시간 계산 (현재 시간으로부터 3일 전)
        // LocalDateTime limitTime = ...
        List<Notification> notifications = notificationRepository.findByReceiverUidAndCreatedAtAfterOrderByCreatedAtDesc(receiverUid,LocalDateTime.now().minusDays(3));

        // 2. DB에서 3일치 알림 목록 조회 (Repository 활용)
        // - receiverUid와 limitTime을 파라미터로 넘김
        // List<Notification> notifications = ...


        // 3. 조회된 Entity 리스트를 DTO 리스트로 변환 (Stream API 활용 권장)
        // - 각 Notification 엔티티를 NotificationResponseDTO로 매핑
        // - 필요한 정보: 알림 ID, 보낸 사람 정보(ID, 이미지), 알림 타입, 생성 시간 등
        List<NotificationResponseDTO> dtos = notifications.stream().map(noti -> NotificationResponseDTO.builder()
                .notid(noti.getNotid())
                .notificationType(noti.getNotificationType())
                .senderUid(noti.getSender().getUid())
                .senderProfileImageUrl(noti.getSender().getProfileImageUrl())
                .senderSnapiaId(noti.getSender().getSnapiaId())
                .build()
        )
                .toList();
    /*
    List<NotificationResponseDTO> dtos = notifications.stream()
            .map(noti -> NotificationResponseDTO.builder()
                .notid(noti.getNotid())
                // ... 나머지 필드 채우기
                .build())
            .collect(Collectors.toList());
    */

        // 4. 최종 변환된 DTO 리스트 반환
        // return dtos;
        return dtos;
    }
}
