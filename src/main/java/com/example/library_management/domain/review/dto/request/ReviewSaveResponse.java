package com.example.library_management.domain.review.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReviewSaveResponse {
    private final Integer reviewStar ;
    private final String reviewTitle;
    private final String reviewDescription;
}
