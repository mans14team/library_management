package com.example.library_management.domain.review.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NONE // 타입 정보를 사용하지 않음
)
@Getter
@RequiredArgsConstructor
public class ReviewUpdateResponse {
    private final Long bookId;
    private final Long reviewId;
    private final Integer reviewStar;
    private final String reviewTitle;
    private final String reviewDescription;
    private final LocalDateTime createAt;
    private final LocalDateTime modifiedAt;
}
