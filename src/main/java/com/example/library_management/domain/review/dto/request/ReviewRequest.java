package com.example.library_management.domain.review.dto.request;

import lombok.Getter;

@Getter
public class ReviewRequest {
    private Integer reviewStar;
    private String reviewTitle;
    private String reviewDescription;
}
