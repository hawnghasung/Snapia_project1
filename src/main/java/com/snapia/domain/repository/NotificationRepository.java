package com.snapia.domain.repository;

import com.snapia.domain.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 1. 특정 유저의 '최근 N일' 알림만 조회 (최신순)
    // 서비스에서 LocalDateTime.now().minusDays(3)을 넘겨주면 됩니다.
    List<Notification> findByReceiverUidAndCreatedAtAfterOrderByCreatedAtDesc(Long receiverUid, LocalDateTime threshold);

    // 2. 특정 팔로우 요청과 연결된 알림 삭제 (기존 기능)
    void deleteByFollowListFid(Long followListFid);

    // 3. 시스템 관리용: 특정 시점보다 오래된 모든 알림 삭제 (청소 로직)
    // 서비스의 스케줄러에서 3일 전 시간을 파라미터로 넣어 호출합니다.
    void deleteByCreatedAtBefore(LocalDateTime threshold);

    // 4. (선택사항) 특정 유저의 모든 알림을 '읽음' 처리할 때 사용
    // @Modifying과 @Query를 조합하여 사용자의 모든 isRead 상태를 한 번에 업데이트할 수 있습니다.
}