package com.example.library_management.domain.review.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NONE // 타입 정보를 사용하지 않음
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewSaveRequest {
    private Integer reviewStar;
    private String reviewTitle;
    private String reviewDescription;
}
