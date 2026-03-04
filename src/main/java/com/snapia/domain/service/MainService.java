package com.snapia.domain.service;

import com.snapia.domain.dto.PostDTO;
import com.snapia.domain.dto.UserDTO;
import com.snapia.domain.entity.Post;
import com.snapia.domain.entity.Users;
import com.snapia.domain.repository.FollowListRepository;
import com.snapia.domain.repository.PostRepository;
import com.snapia.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Log4j2
public class MainService {
    private final PostRepository postRepository;
    private final FollowListRepository followListRepository;
    private final UserRepository userRepository;

    // 1. 피드 게시물 조회
    public List<PostDTO> getFeedPosts(Long userId) {
        // 내가 팔로우하는 사람들의 uid 목록
        List<Long> followingUserIds = followListRepository.findFollowingUserIds(userId);

        if (followingUserIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 그 사람들의 게시글 최신순 조회
        List<Post> posts = postRepository.findByUsersUidInOrderByCreatedAtDesc(followingUserIds);

        // PostDTO로 변환
        return posts.stream()
                .map(post -> PostDTO.builder()
                        .pid(post.getPid())
                        .content(post.getContent())
                        .postImgUrl(post.getPostImgUrl())
                        .pubOrNot(post.getPubOrNot())
                        .userUid(post.getUsers().getUid())
                        .userSnapiaId(post.getUsers().getSnapiaId())
                        .userProfileImageUrl(post.getUsers().getProfileImageUrl())
                        .likeCount(0)
                        .commentCount(0)
                        .build())
                .collect(Collectors.toList());
    }

    // 2. 추천 유저 조회
    public List<Users> getRecommendedUsers(Long userId) {
        List<Long> followingUserIds = followListRepository.findFollowingUserIds(userId);
        followingUserIds.add(userId);
        return userRepository.findTop10ByUidNotInOrderByUidDesc(followingUserIds);
    }

    // 3. 유저 검색
    public List<UserDTO> searchUser(String word) {
        Pageable pageable = PageRequest.of(0, 5);
        List<Users> searchResult = userRepository.findBySnapiaIdContainingOrNameContaining(word, word, pageable);

        if (searchResult.isEmpty()) {
            return Collections.emptyList();
        }

        List<UserDTO> list = new ArrayList<>();
        for (Users users : searchResult) {
            UserDTO userDTO = new UserDTO();
            userDTO.setSnapiaId(users.getSnapiaId());
            userDTO.setName(users.getName());
            userDTO.setProfileImageUrl(users.getProfileImageUrl());
            list.add(userDTO);
        }

        return list;
    }
}