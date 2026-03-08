package com.snapia.domain.controller;

import com.snapia.domain.auth.PrincipalDetails;
import com.snapia.domain.dto.HighlightDTO;
import com.snapia.domain.entity.Highlight;
import com.snapia.domain.repository.HighlightRepository;
import com.snapia.domain.service.HighlightService;
import com.snapia.domain.service.StoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/highlight")
@Log4j2
public class HighlightController {
    private final HighlightService highlightService;
    private final HighlightRepository highlightRepository;

    @GetMapping("/list")
    @ResponseBody
    public List<HighlightDTO> highlightList(@AuthenticationPrincipal PrincipalDetails principalDetails) {

        return highlightService.viewHighLight(principalDetails.getUsers().getUid());

    }

    @GetMapping("/viewer")
    public String highlightView(Model model, Long hid) {
        model.addAttribute("hid",hid );
        return "/highlight/viewer";
    }

    @PostMapping
    @ResponseBody
    public void createHighlight(@RequestBody HighlightDTO highlightDTO,
                                               @AuthenticationPrincipal PrincipalDetails principalDetails) {
        highlightService.createHighLight(highlightDTO,principalDetails.getUsers().getUid());
    }

    @PutMapping("/{hid}")
    @ResponseBody
    public void updateHighlight(@PathVariable Long hid,
                                               @RequestBody HighlightDTO highlightDTO,
                                               @AuthenticationPrincipal PrincipalDetails principalDetails) {
        highlightService.updateHighLight(hid,highlightDTO,principalDetails.getUsers().getUid());
    }

    @DeleteMapping("/{hid}")
    @ResponseBody
    public void deleteHighlight(@PathVariable Long hid,
                                               @AuthenticationPrincipal PrincipalDetails principalDetails) {
        highlightService.deleteHighLight(hid, principalDetails.getUsers().getUid());
    }

    @GetMapping("/{hid}")
    @ResponseBody
    public HighlightDTO getHighlight(@PathVariable Long hid,
                                     @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return highlightService.viewEachHighLight(hid, principalDetails.getUsers().getUid());
    }

    @GetMapping("/{hid}/owner")
    @ResponseBody
    public Map<String, Boolean> isOwner(@PathVariable Long hid,
                                        @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long uid = principalDetails.getUsers().getUid();
        boolean owner = highlightRepository.findByHidAndUsersUid(hid, uid).isPresent();
        return Map.of("isOwner", owner);
    }

    @GetMapping("/user/{uid}")
    @ResponseBody
    public List<HighlightDTO> userHighLightView(@PathVariable Long uid) {
        List<HighlightDTO> highlightDTOS = highlightService.viewHighLight(uid);

        return highlightDTOS;
    }

}
