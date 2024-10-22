package com.example.library_management.domain.review.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ReviewsGetResponse {
    private final Long bookId;
    private final Integer reviewStar;
    private final String reviewTitle;
    private final String reviewDescription;
    private final LocalDateTime createAt;
    private final LocalDateTime modifiedAt;
}
