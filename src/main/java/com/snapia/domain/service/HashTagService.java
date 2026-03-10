package com.snapia.domain.service;

import com.snapia.domain.entity.HashTag;
import com.snapia.domain.entity.Post;
import com.snapia.domain.entity.PostHashTag;
import com.snapia.domain.repository.HashTagRepository;
import com.snapia.domain.repository.PostHashTagRepository;
import com.snapia.domain.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class HashTagService {
    private final HashTagRepository hashTagRepository;
    private final PostHashTagRepository postHashTagRepository;

    public void saveHashTag(List<String> tagNames, Post post) {

        if (tagNames == null || tagNames.isEmpty()) return;

        tagNames.forEach(tagName -> {
            HashTag hashTag = hashTagRepository.findByHashTag(tagName)
                    .orElseGet(() -> hashTagRepository.save(
                            HashTag.builder()
                                    .hashTag(tagName)
                                    .build()
                    ));

            PostHashTag postHashTag = PostHashTag.builder()
                    .post(post)
                    .hashTag(hashTag)
                    .build();


            postHashTagRepository.save(postHashTag);
        });

    }


    public List<String> searchHashTag(String name) {

        List<String> hashTags = hashTagRepository.findByHashTagContaining(name).stream()
                .map( HashTag::getHashTag )
                .toList();


        return hashTags;
    }


}
