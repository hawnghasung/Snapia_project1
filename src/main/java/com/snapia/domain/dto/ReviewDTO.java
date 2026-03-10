package com.snapia.domain.dto;

import com.snapia.domain.entity.BusinessProfile;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDTO {
    private Long rvid;

    private Integer rating;


    private Long businessProfile_id;
}
