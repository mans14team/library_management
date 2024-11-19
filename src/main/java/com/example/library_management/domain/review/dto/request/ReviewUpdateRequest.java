package com.example.library_management.domain.review.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewUpdateRequest {
    private Integer reviewStar;
    private String reviewTitle;
    private String reviewDescription;
}
