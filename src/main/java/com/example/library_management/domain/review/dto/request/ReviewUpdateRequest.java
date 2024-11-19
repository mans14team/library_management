package com.example.library_management.domain.review.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NONE // 타입 정보를 사용하지 않음
)
@Getter
@NoArgsConstructor
public class ReviewUpdateRequest {
    private Integer reviewStar;
    private String reviewTitle;
    private String reviewDescription;
}
