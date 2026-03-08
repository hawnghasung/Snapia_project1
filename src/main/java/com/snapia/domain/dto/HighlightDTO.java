package com.snapia.domain.dto;

import com.snapia.domain.entity.Story;
import com.snapia.domain.entity.Users;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class HighlightDTO {
    private Long hid;

    private String title;

    private List<Long> storyIds;

    private Long userId;

    private String coverImgUrl;

    private Long coverStoryId;


}
