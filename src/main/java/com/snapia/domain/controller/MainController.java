package com.snapia.domain.controller;

import com.snapia.domain.auth.PrincipalDetails;
import com.snapia.domain.dto.PostDTO;
import com.snapia.domain.dto.StoryDTO;
import com.snapia.domain.dto.UserDTO;
import com.snapia.domain.entity.Users;
import com.snapia.domain.service.MainService;
import com.snapia.domain.service.StoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class MainController {

    private final MainService mainService;
    private final StoryService storyService;

    @GetMapping("/main")
    public String main(Model model,
                       @AuthenticationPrincipal PrincipalDetails principalDetails) {

        if (principalDetails != null) {
            Long userId = principalDetails.getUsers().getUid();

            // 피드 게시물 (PostDTO로 변환됨)
            List<PostDTO> feedPosts = mainService.getFeedPosts(userId);
            model.addAttribute("feedPosts", feedPosts);

            // 추천 유저
            List<Users> recommendedUsers = mainService.getRecommendedUsers(userId);
            model.addAttribute("recommendedUsers", recommendedUsers);

            // 스토리
            List<StoryDTO> stories = storyService.getFollowingStories(userId);
            model.addAttribute("stories", stories);

            log.info("메인 페이지 로드 - userId: {}, 게시물 수: {}, 추천 유저 수: {}",
                    userId, feedPosts.size(), recommendedUsers.size());
        }

        return "main";
    }

    @GetMapping("/search")
    @ResponseBody
    public List<UserDTO> searchList(@RequestParam String word) {
        return mainService.searchUser(word);
    }
}