package com.snapia.domain.service;

import com.snapia.domain.constant.FollowStatus;
import com.snapia.domain.constant.NotificationType;
import com.snapia.domain.constant.PubOrNot;
import com.snapia.domain.dto.FollowListDTO;
import com.snapia.domain.dto.NotificationDTO;
import com.snapia.domain.entity.FollowList;
import com.snapia.domain.entity.Users;
import com.snapia.domain.repository.FollowListRepository;
import com.snapia.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class FollowService {
    private final UserRepository userRepository;
    private final FollowListRepository followListRepository;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    // 1. 팔로우 요청하기
    public void sendFollow(Long fromUserId, Long toUserId) {
        // 자기 자신을 팔로우하는 것 방지
        if (fromUserId.equals(toUserId)) {
            throw new IllegalArgumentException("자기 자신을 팔로우할 수 없습니다");
        }

        // 기존 팔로우 관계 확인
        Optional<FollowList> existingFollow = followListRepository.findByFromUserUidAndToUserUid(fromUserId, toUserId);
        if (existingFollow.isPresent()) {
            FollowStatus existingStatus = existingFollow.get().getFollowStatus();
            if (existingStatus == FollowStatus.ACCEPT || existingStatus == FollowStatus.AWAITING) {
                throw new IllegalArgumentException("이미 팔로우 요청을 보냈거나 팔로우 중입니다");
            }
            // DECLINE 상태인 경우 기존 레코드 삭제 후 재생성
            followListRepository.delete(existingFollow.get());
        }

        // 사용자 조회
        Users fromUserEntity = userRepository.findById(fromUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + fromUserId));

        Users toUserEntity = userRepository.findById(toUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + toUserId));

        // 공개/비공개 계정에 따라 상태 결정
        FollowStatus status = (toUserEntity.getPubOrNot() == PubOrNot.PUB)
                ? FollowStatus.ACCEPT
                : FollowStatus.AWAITING;

        FollowList followList = FollowList.builder()
                .fromUser(fromUserEntity)
                .toUser(toUserEntity)
                .followStatus(status)
                .build();

        followListRepository.save(followList);

        if (status == FollowStatus.AWAITING) {
            // 비공개 계정 - 팔로우 요청 알림
            NotificationDTO notificationDTO = NotificationDTO.builder()
                    .senderId(fromUserId)
                    .receiverId(toUserId)
                    .followListId(followList.getFid())
                    .notificationType(NotificationType.FOLLOW_REQUEST)
                    .build();

            notificationService.saveNotification(notificationDTO);
        } else if (status == FollowStatus.ACCEPT) {
            // 공개 계정 - 즉시 팔로우 완료 알림
            NotificationDTO notificationDTO = NotificationDTO.builder()
                    .senderId(fromUserId)
                    .receiverId(toUserId)
                    .followListId(followList.getFid())
                    .notificationType(NotificationType.FOLLOW_ACCEPTED)
                    .build();

            notificationService.saveNotification(notificationDTO);

            // 실시간 카운트 업데이트
            broadcastFollowCount(toUserId);
            broadcastFollowCount(fromUserId);
        }
    }

    // 2. 팔로우 요청 수락
    public void acceptFollow(Long fromUserId, Long toUserId) {
        FollowList followList = followListRepository.findByFromUserUidAndToUserUid(fromUserId, toUserId)
                .orElseThrow(() -> new IllegalArgumentException("팔로우 요청을 찾을 수 없습니다"));

        if (followList.getFollowStatus() != FollowStatus.AWAITING) {
            throw new IllegalArgumentException("대기 중인 요청이 아닙니다");
        }

        followList.setFollowStatus(FollowStatus.ACCEPT);
        followListRepository.save(followList);

        // 기존 FOLLOW_REQUEST 알림 삭제 (선택사항)
        notificationService.delNotification(followList.getFid());

        // 새로운 FOLLOW_ACCEPTED 알림 생성
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .senderId(toUserId)
                .receiverId(fromUserId)
                .followListId(followList.getFid())
                .notificationType(NotificationType.FOLLOW_ACCEPTED)
                .build();

        notificationService.saveNotification(notificationDTO);

        // 실시간 카운트 업데이트
        broadcastFollowCount(toUserId);
        broadcastFollowCount(fromUserId);
    }

    // 3. 팔로우 요청 거절
    public void declineFollow(Long fromUserId, Long toUserId) {
        FollowList followList = followListRepository.findByFromUserUidAndToUserUid(fromUserId, toUserId)
                .orElseThrow(() -> new IllegalArgumentException("팔로우 요청을 찾을 수 없습니다"));

        if (followList.getFollowStatus() != FollowStatus.AWAITING) {
            throw new IllegalArgumentException("대기 중인 요청이 아닙니다");
        }

        Long followListId = followList.getFid();

        // 먼저 알림 삭제
        notificationService.delNotification(followListId);

        // 그 다음 팔로우 관계 삭제
        followListRepository.delete(followList);
    }

    // 4. 언팔로우하기
    public void deleteFollow(Long fromUserId, Long toUserId) {
        // 팔로우 관계 확인 및 조회
        FollowList followList = followListRepository.findByFromUserUidAndToUserUid(fromUserId, toUserId)
                .orElseThrow(() -> new IllegalArgumentException("팔로우 관계를 찾을 수 없습니다"));

        Long followListId = followList.getFid();

        // 알림이 있다면 삭제
        try {
            notificationService.delNotification(followListId);
        } catch (Exception e) {
            log.warn("언팔로우 시 알림 삭제 실패 (알림이 없을 수 있음): {}", e.getMessage());
        }

        // 팔로우 관계 삭제
        followListRepository.delete(followList);

        // 실시간 카운트 업데이트
        broadcastFollowCount(toUserId);
        broadcastFollowCount(fromUserId);
    }

    // 5. 팔로워 리스트 조회 (ACCEPT 상태만)
    public List<FollowListDTO> findFollowerList(Long principalId, Long pageOwnerId) {
        return followListRepository.findFollowerList(principalId, pageOwnerId);
    }

    // 6. 팔로잉 리스트 조회 (ACCEPT 상태만)
    public List<FollowListDTO> findFollowingList(Long principalId, Long pageOwnerId) {
        return followListRepository.findFollowingList(principalId, pageOwnerId);
    }

    // 7. 팔로우 요청 대기 목록 조회 (AWAITING 상태만)
    public List<FollowListDTO> findPendingFollowRequests(Long userId) {
        return followListRepository.findPendingFollowRequests(userId);
    }

    // 8. 팔로우 상태 확인 (ACCEPT 상태만 true)
    public boolean isFollowing(Long fromUserId, Long toUserId) {
        return followListRepository.existsByFromUserUidAndToUserUidAndFollowStatus(
                fromUserId, toUserId, FollowStatus.ACCEPT);
    }

    // 9. 팔로워 수 조회 (ACCEPT 상태만)
    public long getFollowerCount(Long userId) {
        return followListRepository.countByToUserUidAndFollowStatus(userId, FollowStatus.ACCEPT);
    }

    // 10. 팔로잉 수 조회 (ACCEPT 상태만)
    public long getFollowingCount(Long userId) {
        return followListRepository.countByFromUserUidAndFollowStatus(userId, FollowStatus.ACCEPT);
    }

    // 11. 실시간 팔로우 카운트 브로드캐스트
    private void broadcastFollowCount(Long userId) {
        try {
            long followerCount = getFollowerCount(userId);
            long followingCount = getFollowingCount(userId);

            Map<String, Object> countData = new HashMap<>();
            countData.put("followerCount", followerCount);
            countData.put("followingCount", followingCount);

            // WebSocket으로 해당 유저의 프로필 페이지에 브로드캐스트
            simpMessagingTemplate.convertAndSend(
                    "/s/follow/count/" + userId,
                    Optional.of(countData)
            );

            log.info("팔로우 카운트 브로드캐스트 성공 - userId: {}, follower: {}, following: {}",
                    userId, followerCount, followingCount);
        } catch (Exception e) {
            log.error("팔로우 카운트 브로드캐스트 실패 - userId: {}, error: {}", userId, e.getMessage(), e);
        }
    }
}