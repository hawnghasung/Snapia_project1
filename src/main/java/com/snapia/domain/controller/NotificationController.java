package com.snapia.domain.controller;

import com.snapia.domain.dto.NotificationResponseDTO;
import com.snapia.domain.entity.Notification;
import com.snapia.domain.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/noti")
@RestController
public class NotificationController {
    private final NotificationService notificationService;

    // 최근 3일 알림 조회 API
    // @GetMapping("/{receiverUid}")
    // public ResponseEntity<List<NotificationResponseDTO>> getRecentNotifications(@PathVariable Long receiverUid) {
    // 1. 서비스 호출 (아까 만든 NotificationSearch 실행)
    // 2. 결과를 ResponseEntity에 담아 200 OK와 함께 반환
    // }
    @GetMapping("/{receiverUid}")
    public List<NotificationResponseDTO> getNotifications (@PathVariable Long receiverUid) {
        List<NotificationResponseDTO> notifications = notificationService.NotificationSearch(receiverUid);

        return notifications;
    }
}
