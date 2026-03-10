package com.snapia.domain.controller;

import com.snapia.domain.service.HashTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/hashtag")
@RequiredArgsConstructor
public class HashTagController {
    private final HashTagService hashTagService;

    @GetMapping("/search")
    public List<String> searchHashTag(@RequestParam String keyword) {
        List<String> result = hashTagService.searchHashTag(keyword);


        return result;
    }
}
