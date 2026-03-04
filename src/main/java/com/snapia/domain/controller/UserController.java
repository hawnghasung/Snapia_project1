package com.snapia.domain.controller;

import com.snapia.domain.auth.PrincipalDetails;
import com.snapia.domain.dto.ProfileDTO;
import com.snapia.domain.dto.ProfilePostDTO;
import com.snapia.domain.dto.StoryDTO;
import com.snapia.domain.dto.UserDTO;
import com.snapia.domain.entity.Users;
import com.snapia.domain.service.FollowService;
import com.snapia.domain.service.StoryService;
import com.snapia.domain.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class UserController {
    private final UserService userService;
    private final FollowService followService;
    private final StoryService storyService;

    // 회원가입
    @GetMapping("/join")
    public String signUpGet(UserDTO userDTO) {
        return "main/join";
    }

    @PostMapping("/join")
    public String signUpPost(@Valid UserDTO userDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info("회원 가입 실패 : " + bindingResult.hasErrors());
            return "main/join";
        }
        userService.signUp(userDTO);
        return "redirect:/main";
    }

    // 로그인
    @GetMapping("/login")
    public String login() {
        return "main/login";
    }

    // 내 프로필 조회
    @GetMapping("/profile")
    public String myProfile(Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails == null) return "redirect:/login";

        Users user = principalDetails.getUsers();
        model.addAttribute("user", user);

        List<ProfilePostDTO> posts = userService.getProfilePosts(user.getUid());
        model.addAttribute("posts", posts);

        setStoryAttributes(model, user.getUid());

        return "main/profile";
    }

    // 프로필 수정 폼
    @GetMapping("/profile/update")
    public String updateGet(Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails == null) return "redirect:/login";

        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setBio(principalDetails.getUsers().getBio());
        profileDTO.setGender(principalDetails.getUsers().getGender());
        profileDTO.setSnapiaId(principalDetails.getUsers().getSnapiaId());
        profileDTO.setProfileImageUrl(principalDetails.getUsers().getProfileImageUrl());
        profileDTO.setName(principalDetails.getUsers().getName());
        profileDTO.setEmail(principalDetails.getUsers().getEmail());

        model.addAttribute("profile", profileDTO);
        return "main/update";
    }

    // 프로필 수정 처리
    @PostMapping("/profile/update")
    public String updatePost(ProfileDTO profileDTO, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails == null) return "redirect:/login";

        userService.profileUpdate(profileDTO, principalDetails.getUsers().getUid());
        principalDetails.getUsers().setSnapiaId(profileDTO.getSnapiaId());
        principalDetails.getUsers().setName(profileDTO.getName());
        principalDetails.getUsers().setBio(profileDTO.getBio());
        principalDetails.getUsers().setEmail(profileDTO.getEmail());
        principalDetails.getUsers().setProfileImageUrl(profileDTO.getProfileImageUrl());

        return "redirect:/profile";
    }

    // 타인 프로필 조회
    @GetMapping("/profile/{snapiaId}")
    public String userProfile(@PathVariable String snapiaId,
                              @AuthenticationPrincipal PrincipalDetails principalDetails,
                              Model model) {
        Users user = userService.findBySnapiaId(snapiaId);
        boolean isMyProfile = principalDetails.getUsers().getUid().equals(user.getUid());

        List<ProfilePostDTO> posts = userService.getProfilePosts(user.getUid());

        if (!isMyProfile) {
            boolean isFollowing = followService.isFollowing(
                    principalDetails.getUsers().getUid(), user.getUid());
            model.addAttribute("isFollowing", isFollowing);
        }

        model.addAttribute("followerCount", followService.getFollowerCount(user.getUid()));
        model.addAttribute("followingCount", followService.getFollowingCount(user.getUid()));
        model.addAttribute("isMyProfile", isMyProfile);
        model.addAttribute("user", user);
        model.addAttribute("posts", posts);

        setStoryAttributes(model, user.getUid());

        return "main/profile";
    }

    // 스토리 관련 model 세팅 공통 메서드
    private void setStoryAttributes(Model model, Long uid) {
        List<StoryDTO> stories = storyService.getMyStories(uid);
        boolean hasStory = !stories.isEmpty();
        model.addAttribute("hasStory", hasStory);
        if (hasStory) {
            model.addAttribute("latestStoryId", stories.get(0).getSid());
        }
    }
}