package com.snapia.domain.controller;

import com.snapia.domain.dto.SuggestDTO;
import com.snapia.domain.service.SuggestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/suggest")
@RequiredArgsConstructor
public class SuggestController {
    private final SuggestService suggestService;

    @PostMapping("/create")
    public SuggestDTO createSuggest(SuggestDTO suggestDTO) {
        return suggestService.createSuggest(suggestDTO);
    }

    @PostMapping("/accept")
    public SuggestDTO acceptSuggest(SuggestDTO suggestDTO) {
        return suggestService.acceptSuggest(suggestDTO.getAppointmentId());
    }

    @PostMapping("/reject")
    public SuggestDTO rejectSuggest(SuggestDTO suggestDTO) {
        return suggestService.rejectSuggest(suggestDTO.getAppointmentId(), suggestDTO.getRejectReason());
    }
}
