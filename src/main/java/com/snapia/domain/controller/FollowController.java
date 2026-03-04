package com.snapia.domain.controller;

import com.snapia.domain.auth.PrincipalDetails;
import com.snapia.domain.dto.FollowListDTO;
import com.snapia.domain.service.FollowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/follow")
@Log4j2
public class FollowController {
    private final FollowService followService;

    // 팔로우 요청 (AJAX)
    @PostMapping("/{toUserId}")
    @ResponseBody
    public Map<String, Object> sendFollow(@PathVariable Long toUserId,
                                          @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long fromUserId = principalDetails.getUsers().getUid();

        Map<String, Object> result = new HashMap<>();
        try {
            followService.sendFollow(fromUserId, toUserId);
            result.put("success", true);
            result.put("message", "팔로우 요청을 보냈습니다");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }

        return result;
    }

    // 언팔로우 (AJAX)
    @DeleteMapping("/{toUserId}")
    @ResponseBody
    public Map<String, Object> deleteFollow(@PathVariable Long toUserId,
                                            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long fromUserId = principalDetails.getUsers().getUid();

        Map<String, Object> result = new HashMap<>();
        try {
            followService.deleteFollow(fromUserId, toUserId);
            result.put("success", true);
            result.put("message", "언팔로우했습니다");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }

        return result;
    }

    // 팔로우 요청 수락 (AJAX)
    @PostMapping("/{fromUserId}/accept")
    @ResponseBody
    public Map<String, Object> acceptFollow(@PathVariable Long fromUserId,
                                            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long toUserId = principalDetails.getUsers().getUid();

        Map<String, Object> result = new HashMap<>();
        try {
            followService.acceptFollow(fromUserId, toUserId);
            result.put("success", true);
            result.put("message", "팔로우 요청을 수락했습니다");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }

        return result;
    }

    // 팔로우 요청 거절 (AJAX)
    @PostMapping("/{fromUserId}/decline")
    @ResponseBody
    public Map<String, Object> declineFollow(@PathVariable Long fromUserId,
                                             @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long toUserId = principalDetails.getUsers().getUid();

        Map<String, Object> result = new HashMap<>();
        try {
            followService.declineFollow(fromUserId, toUserId);
            result.put("success", true);
            result.put("message", "팔로우 요청을 거절했습니다");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }

        return result;
    }

    // 팔로워 리스트 페이지
    @GetMapping("/followers/{userId}")
    public String followerList(@PathVariable Long userId,
                               Model model,
                               @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long principalId = principalDetails.getUsers().getUid();

        List<FollowListDTO> followers = followService.findFollowerList(principalId, userId);
        model.addAttribute("followers", followers);
        model.addAttribute("pageOwnerId", userId);

        return "main/follow/followers";
    }

    // 팔로잉 리스트 페이지
    @GetMapping("/following/{userId}")
    public String followingList(@PathVariable Long userId,
                                Model model,
                                @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long principalId = principalDetails.getUsers().getUid();

        List<FollowListDTO> following = followService.findFollowingList(principalId, userId);
        model.addAttribute("following", following);
        model.addAttribute("pageOwnerId", userId);

        return "main/follow/following";
    }

    // 팔로우 요청 대기 목록
    @GetMapping("/requests")
    public String pendingRequests(Model model,
                                  @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long userId = principalDetails.getUsers().getUid();

        List<FollowListDTO> requests = followService.findPendingFollowRequests(userId);
        model.addAttribute("requests", requests);

        return "main/follow/requests";
    }

    // 팔로워 목록 JSON API (팝업용)
    @GetMapping("/followers/data/{userId}")
    @ResponseBody
    public List<FollowListDTO> getFollowersData(@PathVariable Long userId,
                                                @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long principalId = principalDetails.getUsers().getUid();
        return followService.findFollowerList(principalId, userId);
    }

    // 팔로잉 목록 JSON API (팝업용)
    @GetMapping("/following/data/{userId}")
    @ResponseBody
    public List<FollowListDTO> getFollowingData(@PathVariable Long userId,
                                                @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long principalId = principalDetails.getUsers().getUid();
        return followService.findFollowingList(principalId, userId);
    }

    // 팔로우 상태 확인 API (AJAX)
    @GetMapping("/status/{userId}")
    @ResponseBody
    public Map<String, Object> getFollowStatus(@PathVariable Long userId,
                                               @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long principalId = principalDetails.getUsers().getUid();

        Map<String, Object> result = new HashMap<>();
        result.put("isFollowing", followService.isFollowing(principalId, userId));
        result.put("followerCount", followService.getFollowerCount(userId));
        result.put("followingCount", followService.getFollowingCount(userId));

        return result;
    }
}