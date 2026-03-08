package com.snapia.domain.service;

import com.snapia.domain.dto.HighlightDTO;
import com.snapia.domain.dto.StoryDTO;
import com.snapia.domain.entity.Highlight;
import com.snapia.domain.entity.Story;
import com.snapia.domain.entity.Users;
import com.snapia.domain.repository.HighlightRepository;
import com.snapia.domain.repository.StoryRepository;
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
public class HighlightService {
    private final HighlightRepository highlightRepository;
    private final UserRepository userRepository;
    private final StoryRepository storyRepository;

    //하이라이트 생성
    public void createHighLight(HighlightDTO highlightDTO, Long uid) {
        Users users = userRepository.findById(uid)
                .orElseThrow(()->new IllegalArgumentException("오류"));

        List<Story> stories = storyRepository.findAllById(highlightDTO.getStoryIds());

        Highlight highlight = Highlight.builder()
                .title(highlightDTO.getTitle())
                .content(stories)
                .coverImgUrl(highlightDTO.getCoverImgUrl())
                .users(users)
                .build();

        highlightRepository.save(highlight);

    }

    //하이라이트 조회
    public List<HighlightDTO> viewHighLight(Long uid) {
        List<Highlight> highlight = highlightRepository.findByUsersUid(uid);





        return highlight.stream()
                .map(highlight1 -> {
                    HighlightDTO dto = new HighlightDTO();
                    dto.setHid(highlight1.getHid());
                    dto.setTitle(highlight1.getTitle());
                    dto.setCoverImgUrl(highlight1.getCoverImgUrl());
                    dto.setStoryIds(highlight1.getContent().stream().map(Story::getSid).toList());

                    return dto;
                })
                .toList();
    }

    //하이라이트 개별 조회
    public HighlightDTO viewEachHighLight(Long hid, Long uid) {
        Highlight highlights = highlightRepository.findByHidAndUsersUid(hid, uid)
                .orElseThrow(()->new IllegalArgumentException("오류"));

        HighlightDTO highlightDTO = new HighlightDTO();

        highlightDTO.setHid(highlights.getHid());
        highlightDTO.setTitle(highlights.getTitle());
        highlightDTO.setUserId(uid);
        highlightDTO.setStoryIds(highlights.getContent().stream().map(Story::getSid).toList());
        highlightDTO.setCoverImgUrl(highlights.getCoverImgUrl());


        return highlightDTO;
    }

    //하이라이트 삭제
    public void deleteHighLight(Long hid, Long uid) {
        Highlight highlight = highlightRepository.findByHidAndUsersUid(hid,uid)
                .orElseThrow(() -> new IllegalArgumentException("오류"));

        highlightRepository.delete(highlight);
    }

    //하이라이트 수정
    /*
      하이라이트 제목 수정
      스토리 추가/제거
      커버 이미지 수정
    * */
    public void updateHighLight(Long hid, HighlightDTO highlightDTO, Long uid) {

    }

}
