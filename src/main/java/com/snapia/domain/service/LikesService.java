package com.snapia.domain.service;

import com.snapia.domain.entity.Likes;
import com.snapia.domain.entity.Post;
import com.snapia.domain.entity.Users;
import com.snapia.domain.repository.LikesRepository;
import com.snapia.domain.repository.PostRepository;
import com.snapia.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class LikesService {
    private final LikesRepository likesRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public void toggleLike(Long postId, String snapiaId) {
        Users users = userRepository.findByLoginIdentifier(snapiaId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 조회 불가"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물 조회 불가"));

        likesRepository.findByPostPidAndUsersUid(postId, users.getUid())
                .ifPresentOrElse(
                        likesRepository::delete,
                        () -> {
                            Likes likes = Likes.builder()
                                    .post(post)
                                    .users(users)
                                    .build();
                            likesRepository.save(likes);
                        }
                );
    }

    public Long getLikeCount(Long postId) {
        return likesRepository.countByPostPid(postId);
    }

    public boolean isLikedByUser(Long postId, String snapiaId) {
        Users users = userRepository.findByLoginIdentifier(snapiaId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 조회 불가"));

        return likesRepository.existsByPostPidAndUsersUid(postId, users.getUid());
    }
}