package com.snapia.domain.controller;

import com.snapia.domain.auth.PrincipalDetails;
import com.snapia.domain.dto.StoryDTO;
import com.snapia.domain.entity.Story;
import com.snapia.domain.service.StoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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

    // 스토리 등록 (파일 저장 + DB 등록 통합)
    @PostMapping
    @ResponseBody
    public Map<String, Object> createStory(@RequestParam("file") MultipartFile file,
                                           @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 1. 파일 저장 처리
            String uploadPath = "C:/snapia/upload/";
            String originalName = file.getOriginalFilename();
            String fileName = UUID.randomUUID().toString() + "_" + originalName;
            File saveFile = new File(uploadPath, fileName);

            if (!saveFile.getParentFile().exists()) saveFile.getParentFile().mkdirs();
            file.transferTo(saveFile);

            // 2. 서비스 호출
            StoryDTO storyDTO = new StoryDTO();
            storyDTO.setStoryImgUrl(fileName);

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

    @GetMapping("/following")
    @ResponseBody
    public List<StoryDTO> getFollowingStories(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        return storyService.getFollowingStories(principalDetails.getUsers().getUid());
    }

    @GetMapping("/detail/{sid}")
    @ResponseBody
    public StoryDTO getStoryDetail(@PathVariable Long sid) {
        return storyService.getStoryDetail(sid);
    }

    @GetMapping("/{sid}")
    public String viewStory(@PathVariable Long sid, Model model) {
        model.addAttribute("storyId", sid);
        return "main/story/viewer";
    }

    @DeleteMapping("/{sid}")
    @ResponseBody
    public Map<String, Object> deleteStory(@PathVariable Long sid, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Map<String, Object> result = new HashMap<>();
        try {
            storyService.deleteStory(sid, principalDetails.getUsers().getSnapiaId());
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
        }
        return result;
    }
}