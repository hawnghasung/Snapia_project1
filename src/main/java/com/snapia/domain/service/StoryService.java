package com.snapia.domain.service;

import com.snapia.domain.dto.FollowListDTO;
import com.snapia.domain.dto.StoryDTO;
import com.snapia.domain.dto.UserDTO;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class StoryService {
    private final StoryRepository storyRepository;
    private final FollowListRepository followListRepository;
    private final UserRepository userRepository;
    private final FileService fileService;

    // 1. 스토리 등록
    public Story createStory(StoryDTO storyDTO, String snapiaId) {
        Users user = (Users) userRepository.findBySnapiaId(snapiaId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        LocalDateTime expiresAt = LocalDateTime.now().plusHours(24);

        // ✅ 기존 코드 교체
        String mediaType = fileService.getMediaType(storyDTO.getMultipartFile());
        String mediaUrl;
        String thumbnailUrl = null;
        Integer duration = null;

        if ("VIDEO".equals(mediaType)) {
            FileService.VideoSaveResult result = fileService.saveVideo(storyDTO.getMultipartFile(), 15); // 스토리는 15초
            mediaUrl     = result.videoFileName;
            thumbnailUrl = result.thumbFileName;
            duration     = result.duration;
        } else {
            mediaUrl = fileService.saveFile(storyDTO.getMultipartFile());
        }

        Story story = Story.builder()
                .users(user)
                .storyImgUrl(mediaUrl)      // 기존 필드 그대로 사용
                .mediaType(mediaType)       // ✅ 추가
                .thumbnailUrl(thumbnailUrl) // ✅ 추가
                .duration(duration)         // ✅ 추가
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

        //조회하면 조회한 유저에 추가되게
        stories.forEach(story -> {
            if (!story.getUsers().getUid().equals(userId)) {
                // viewUsers에 현재 유저 없으면 추가
                Users users = userRepository.findById(userId)
                        .orElseThrow(()->new IllegalArgumentException("오류"));

                if (!story.getViewUser().contains(users)) {
                    story.getViewUser().add(users);


                    storyRepository.save(story);
                }



            }
        });


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
    public StoryDTO getStoryDetail(Long sid, Long userId) {
        Story story = storyRepository.findById(sid)
                .orElseThrow(() -> new IllegalArgumentException("스토리를 찾을 수 없습니다"));

        if (!story.getUsers().getUid().equals(userId)) {
            Users users = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("오류"));
            if (!story.getViewUser().contains(users)) {
                story.getViewUser().add(users);
                storyRepository.save(story);
            }
        }


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

    // 6. 만료된 스토리 자동 보관 (매시간 실행)
    @Scheduled(cron = "0 0 * * * *") // 매시간 정각에 실행
    public void doArchived() {
        /*
        .now()로 현재 시간 가져오기
        방금 추가한 findByExpiresAtBeforeAndIsArchivedFalse(now)로 만료된 스토리 목록 조회
        isEmpty() 체크
        조회된 스토리들 반복문 돌면서 isArchived = true로 변경
        saveAll()로 일괄 저장
        로그 찍기
        */
        LocalDateTime now = LocalDateTime.now();
        List<Story> expiredStories = storyRepository.findByExpiresAtBeforeAndIsArchivedFalse(now);


        expiredStories.forEach(story -> story.setArchived(true));

        storyRepository.saveAll(expiredStories);

        log.info("스토리 만료 저장 : " + expiredStories);
    }

    // 보관된 스토리 조회용 메서드
    public List<StoryDTO> archivedStoryList(Long uid) {
        List<Story> stories = storyRepository.findByUsersUidAndIsArchivedTrue(uid);


        return stories.stream()
                .map(story -> {
                    StoryDTO dto = new StoryDTO();
                    dto.setSid(story.getSid());
                    dto.setCreatedAt(story.getCreatedAt());
                    dto.setStoryImgUrl(story.getStoryImgUrl());
                    dto.setExpiresAt(story.getExpiresAt());
                    dto.setUserId(story.getUsers().getUid());
                    dto.setSnapiaId(story.getUsers().getSnapiaId());

                    return dto;
                })
                .toList();
    }

    //스토리 본 유저 조회 메서드
    public List<UserDTO> usersList(Long sid) {
        Story story = storyRepository.findById(sid)
                .orElseThrow(()->new IllegalArgumentException());



        return story.getViewUser().stream()
                .map(users -> {
                    UserDTO userDTO = new UserDTO();
                    userDTO.setProfileImageUrl(users.getProfileImageUrl());
                    userDTO.setSnapiaId(users.getSnapiaId());

                    return userDTO;
                })
                .toList();
    }

    // DTO 변환 헬퍼 메서드
    private StoryDTO convertToDTO(Story story) {
        return StoryDTO.builder()
                .sid(story.getSid())
                .userId(story.getUsers().getUid())
                .snapiaId(story.getUsers().getSnapiaId())
                .profileImageUrl(story.getUsers().getProfileImageUrl())
                .storyImgUrl(story.getStoryImgUrl())
                .mediaType(story.getMediaType())       // ✅ 추가
                .thumbnailUrl(story.getThumbnailUrl()) // ✅ 추가
                .createdAt(story.getCreatedAt())
                .expiresAt(story.getExpiresAt())
                .build();
    }

}