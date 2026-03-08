package com.snapia.domain.controller;

import com.snapia.domain.auth.PrincipalDetails;
import com.snapia.domain.dto.StoryDTO;
import com.snapia.domain.dto.UserDTO;
import com.snapia.domain.entity.Story;
import com.snapia.domain.entity.Users;
import com.snapia.domain.service.StoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/story")
@Log4j2
public class StoryController {
    private final StoryService storyService;

    @PostMapping
    @ResponseBody
    public Map<String, Object> createStory(@RequestParam("file") MultipartFile file,
                                           @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Map<String, Object> result = new HashMap<>();
        try {
            StoryDTO storyDTO = new StoryDTO();
            storyDTO.setMultipartFile(file); // ✅ 서비스에서 알아서 처리

            String snapiaId = principalDetails.getUsers().getSnapiaId();
            Story story = storyService.createStory(storyDTO, snapiaId);

            result.put("success", true);
            result.put("message", "스토리가 등록되었습니다");
            result.put("storyId", story.getSid());
        } catch (Exception e) {
            log.error("스토리 등록 실패", e);
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    // 팔로잉 스토리 목록
    @GetMapping("/following")
    @ResponseBody
    public List<StoryDTO> getFollowingStories(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        return storyService.getFollowingStories(principalDetails.getUsers().getUid());
    }

    // 스토리 상세 (기존 메서드 대체 — isOwner 포함)
    @GetMapping("/detail/{sid}")
    @ResponseBody
    public StoryDTO getStoryDetail(@PathVariable Long sid,
                                   @AuthenticationPrincipal PrincipalDetails principalDetails) {
        StoryDTO dto = storyService.getStoryDetail(sid, principalDetails.getUsers().getUid());
        if (principalDetails != null) {
            dto.setIsOwner(principalDetails.getUsers().getSnapiaId().equals(dto.getSnapiaId()));
        } else {
            dto.setIsOwner(false);
        }
        return dto;
    }

    // 뷰어 페이지
    @GetMapping("/{sid}")
    public String viewStory(@PathVariable Long sid, Model model) {
        model.addAttribute("storyId", sid);
        return "main/story/viewer";
    }

    // 스토리 삭제
    @DeleteMapping("/{sid}")
    @ResponseBody
    public Map<String, Object> deleteStory(@PathVariable Long sid,
                                           @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Map<String, Object> result = new HashMap<>();
        try {
            storyService.deleteStory(sid, principalDetails.getUsers().getSnapiaId());
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/archive")
    @ResponseBody
    public List<StoryDTO> viewArchiveStory(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<StoryDTO> stories = storyService.archivedStoryList(principalDetails.getUsers().getUid());

        return stories;
    }

    @GetMapping("/archive/list")
    public String viewArchive() {
        return "main/story/archive";
    }

    //스토리 본 유저 확인
    @GetMapping("/{sid}/viewers")
    @ResponseBody
    public List<UserDTO> usersList(@PathVariable Long sid) {
        List<UserDTO> users = storyService.usersList(sid);

        return users;
    }
}