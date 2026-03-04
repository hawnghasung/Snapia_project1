package com.snapia.domain.service;

import com.snapia.domain.dto.FollowListDTO;
import com.snapia.domain.dto.StoryDTO;
import com.snapia.domain.entity.Story;
import com.snapia.domain.entity.Users;
import com.snapia.domain.repository.FollowListRepository;
import com.snapia.domain.repository.StoryRepository;
import com.snapia.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class StoryService {
    private final StoryRepository storyRepository;
    private final FollowListRepository followListRepository;
    private final UserRepository userRepository;

    // 1. 스토리 등록
    public Story createStory(StoryDTO storyDTO, String snapiaId) {
        Users user = (Users) userRepository.findBySnapiaId(snapiaId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        // 24시간 후 만료
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(24);

        Story story = Story.builder()
                .users(user)
                .storyImgUrl(storyDTO.getStoryImgUrl())
                .expiresAt(expiresAt)
                .build();

        return storyRepository.save(story);
    }

    // 2. 팔로잉 스토리 조회 (24시간 이내)
    public List<StoryDTO> getFollowingStories(Long userId) {
        // 내가 팔로우하는 사람들의 uid 리스트 가져오기
        List<Long> followingUserIds = followListRepository.findFollowingList(userId, userId)
                .stream()
                .map(FollowListDTO::getUserId)
                .collect(Collectors.toList());

        // 본인도 포함 (내 스토리도 보여주기)
        followingUserIds.add(userId);

        // 24시간 이내 스토리 조회
        LocalDateTime yesterday = LocalDateTime.now().minusHours(24);
        List<Story> stories = storyRepository.findByUsersUidInAndCreatedAtAfterOrderByCreatedAtDesc(
                followingUserIds, yesterday);

        // DTO로 변환
        return stories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 3. 내 스토리 조회
    public List<StoryDTO> getMyStories(Long userId) {
        LocalDateTime yesterday = LocalDateTime.now().minusHours(24);
        List<Story> stories = storyRepository.findByUsersUidAndCreatedAtAfterOrderByCreatedAtDesc(
                userId, yesterday);

        return stories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ⭐ 4. 특정 스토리 상세 조회
    public StoryDTO getStoryDetail(Long sid) {
        Story story = storyRepository.findById(sid)
                .orElseThrow(() -> new IllegalArgumentException("스토리를 찾을 수 없습니다"));

        return convertToDTO(story);
    }

    // 5. 스토리 삭제
    public void deleteStory(Long sid, String snapiaId) {
        Story story = storyRepository.findById(sid)
                .orElseThrow(() -> new IllegalArgumentException("스토리를 찾을 수 없습니다"));

        // 본인 확인
        if (!story.getUsers().getSnapiaId().equals(snapiaId)) {
            throw new IllegalArgumentException("본인의 스토리만 삭제할 수 있습니다");
        }

        storyRepository.delete(story);
    }

    // 6. 만료된 스토리 자동 삭제 (매시간 실행)
    @Scheduled(cron = "0 0 * * * *") // 매시간 정각에 실행
    public void deleteExpiredStories() {
        LocalDateTime now = LocalDateTime.now();
        List<Story> expiredStories = storyRepository.findByExpiresAtBefore(now);

        if (!expiredStories.isEmpty()) {
            storyRepository.deleteAll(expiredStories);
            log.info("만료된 스토리 {}개 삭제 완료", expiredStories.size());
        }
    }

    // DTO 변환 헬퍼 메서드
    private StoryDTO convertToDTO(Story story) {
        return StoryDTO.builder()
                .sid(story.getSid())
                .userId(story.getUsers().getUid())
                .snapiaId(story.getUsers().getSnapiaId())
                .profileImageUrl(story.getUsers().getProfileImageUrl())
                .storyImgUrl(story.getStoryImgUrl())
                .createdAt(story.getCreatedAt())
                .expiresAt(story.getExpiresAt())
                .build();
    }

    // StoryService.java에 추가
    public boolean hasStory(Long userId) {
        LocalDateTime yesterday = LocalDateTime.now().minusHours(24);
        return storyRepository.existsByUsersUidAndCreatedAtAfter(userId, yesterday);
    }
}