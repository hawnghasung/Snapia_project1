package com.snapia.domain.controller;

import com.snapia.domain.auth.PrincipalDetails;
import com.snapia.domain.dto.CommentResponseDTO;
import com.snapia.domain.dto.PostDTO;
import com.snapia.domain.entity.Comment;
import com.snapia.domain.entity.Post;
import com.snapia.domain.service.CommentService;
import com.snapia.domain.service.LikesService;
import com.snapia.domain.service.PostService;
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
@RequestMapping("/post")
@Log4j2
public class PostController {
    private final PostService postService;
    private final CommentService commentService;
    private final LikesService likesService;

    // 내 게시물 목록
    @GetMapping("/my")
    public String myPost(Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails == null) {
            return "redirect:/login";
        }

        Long userId = principalDetails.getUsers().getUid();
        List<Post> posts = postService.getPostsByUser(userId);
        model.addAttribute("posts", posts);

        return "main/post/posts";
    }

    // 게시물 상세보기
    @GetMapping("/{pid}")
    public String postDetail(@PathVariable Long pid, Model model,
                             @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Post post = postService.getPostById(pid);
        model.addAttribute("post", post);

        List<CommentResponseDTO> comments = commentService.getCommentsByPost(pid, principalDetails.getUsers().getSnapiaId());
        model.addAttribute("comments", comments);

        Long likeCount = likesService.getLikeCount(pid);
        model.addAttribute("likeCount", likeCount);

        if (principalDetails != null) {
            boolean isLiked = likesService.isLikedByUser(pid, principalDetails.getUsers().getSnapiaId());
            model.addAttribute("isLiked", isLiked);
        }

        List<String> hashtags = postService.getHashtags(pid);
        model.addAttribute("hashtags", hashtags);

        return "main/post/detail";
    }

    // 게시물 작성 폼
    @GetMapping("/new")
    public String createForm() {
        return "main/post/createForm";
    }

    // 게시물 등록 → 내 프로필로 이동
    @PostMapping
    public String create(PostDTO postDTO, @RequestParam(value = "hashtags", required = false) List<String> hashtags,
                         @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails == null) {
            return "redirect:/login";
        }

        postDTO.setHashtags(hashtags);

        String snapiaId = principalDetails.getUsers().getSnapiaId();
        postService.register(postDTO, snapiaId);

        return "redirect:/profile/" + snapiaId;
    }

    // 게시물 수정 폼
    @GetMapping("/{pid}/edit")
    public String editForm(@PathVariable Long pid, Model model,
                           @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails == null) {
            return "redirect:/login";
        }

        Post post = postService.getPostById(pid);

        if (!post.getUsers().getSnapiaId().equals(principalDetails.getUsers().getSnapiaId())) {
            throw new IllegalArgumentException("본인의 게시물만 수정할 수 있습니다");
        }

        model.addAttribute("post", post);
        return "main/post/editForm";
    }

    // 게시물 수정 → 내 프로필로 이동
    @PostMapping("/{pid}")
    public String update(@PathVariable Long pid, PostDTO postDTO,
                         @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails == null) {
            return "redirect:/login";
        }

        String snapiaId = principalDetails.getUsers().getSnapiaId();
        postService.update(pid, postDTO, snapiaId);

        return "redirect:/profile/" + snapiaId;
    }

    // 게시물 삭제 → 내 프로필로 이동
    @PostMapping("/{pid}/delete")
    public String delete(@PathVariable Long pid, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails == null) {
            return "redirect:/login";
        }

        String snapiaId = principalDetails.getUsers().getSnapiaId();
        postService.delete(pid, snapiaId);

        return "redirect:/profile/" + snapiaId;
    }

    // 좋아요 토글 (AJAX)
    @PostMapping("/{pid}/like")
    @ResponseBody
    public Map<String, Object> toggleLike(@PathVariable Long pid,
                                          @AuthenticationPrincipal PrincipalDetails principalDetails) {
        String snapiaId = principalDetails.getUsers().getSnapiaId();
        likesService.toggleLike(pid, snapiaId);

        Map<String, Object> result = new HashMap<>();
        result.put("isLiked", likesService.isLikedByUser(pid, snapiaId));
        result.put("likeCount", likesService.getLikeCount(pid));

        return result;
    }

    @GetMapping("/hashtag/result")
    public String searchByHashtag(@RequestParam String tag, Model model,
                                  @AuthenticationPrincipal PrincipalDetails principalDetails) {
        String snapiaId = principalDetails.getUsers().getSnapiaId();
        model.addAttribute("posts", postService.getPostsByHashtag(tag, snapiaId));
        model.addAttribute("tag", tag);
        return "main/post/hashtagResult";
    }


}