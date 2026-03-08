package com.snapia.domain.service;

import com.snapia.domain.constant.PubOrNot;
import com.snapia.domain.dto.PostDTO;
import com.snapia.domain.entity.Post;
import com.snapia.domain.entity.Users;
import com.snapia.domain.repository.PostRepository;
import com.snapia.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class PostService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final FileService fileService;

    public void register(PostDTO postDTO, String snapiaId) {
        Users users = userRepository.findByLoginIdentifier(snapiaId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 조회 불가"));

        // ✅ 기존 코드 교체
        String mediaType = fileService.getMediaType(postDTO.getMultipartFile());
        String mediaUrl;
        String thumbnailUrl = null;
        Integer duration = null;

        if ("VIDEO".equals(mediaType)) {
            FileService.VideoSaveResult result = fileService.saveVideo(postDTO.getMultipartFile(), 60);
            mediaUrl     = result.videoFileName;
            thumbnailUrl = result.thumbFileName;
            duration     = result.duration;
        } else {
            mediaUrl = fileService.saveFile(postDTO.getMultipartFile());
        }

        Post post = Post.builder()
                .content(postDTO.getContent())
                .users(users)
                .pubOrNot(PubOrNot.PUB)
                .postImgUrl(mediaUrl)       // 기존 필드 그대로 사용
                .mediaType(mediaType)       // ✅ 추가
                .thumbnailUrl(thumbnailUrl) // ✅ 추가
                .duration(duration)         // ✅ 추가
                .build();

        postRepository.save(post);
    }

    public Post getPostById(Long pid) {
        return postRepository.findById(pid)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다"));
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public void update(Long pid, PostDTO postDTO, String snapiaId) {
        Post post = postRepository.findById(pid)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다"));

        if (!post.getUsers().getSnapiaId().equals(snapiaId)) {
            throw new IllegalArgumentException("본인 게시물만 수정 가능합니다");
        }

        if (postDTO.getContent() != null) {
            post.setContent(postDTO.getContent());
        }

        if (postDTO.getMultipartFile() != null && !postDTO.getMultipartFile().isEmpty()) {
            // 기존 파일 삭제
            fileService.deleteFile(post.getPostImgUrl());
            if (post.getThumbnailUrl() != null) fileService.deleteFile(post.getThumbnailUrl()); // ✅ 추가

            // ✅ 타입 분기
            String mediaType = fileService.getMediaType(postDTO.getMultipartFile());
            if ("VIDEO".equals(mediaType)) {
                FileService.VideoSaveResult result = fileService.saveVideo(postDTO.getMultipartFile(), 60);
                post.setPostImgUrl(result.videoFileName);
                post.setThumbnailUrl(result.thumbFileName);
                post.setDuration(result.duration);
                post.setMediaType("VIDEO");
            } else {
                post.setPostImgUrl(fileService.saveFile(postDTO.getMultipartFile()));
                post.setThumbnailUrl(null);
                post.setDuration(null);
                post.setMediaType("IMAGE");
            }
        }

        if (postDTO.getPubOrNot() != null) {
            post.setPubOrNot(postDTO.getPubOrNot());
        }
    }

    public void delete(Long pid, String snapiaId) {
        Post post = postRepository.findById(pid)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다"));

        if (!post.getUsers().getSnapiaId().equals(snapiaId)) {
            throw new IllegalArgumentException("본인 게시물만 삭제 가능합니다");
        }

        // 이미지 파일 삭제
        if (post.getPostImgUrl() != null) {
            fileService.deleteFile(post.getPostImgUrl());
        }

        postRepository.delete(post);
    }

    // 특정 사용자의 게시물 목록 조회
    public List<Post> getPostsByUser(Long uid) {
        return postRepository.findByUsersUid(uid);
    }
}