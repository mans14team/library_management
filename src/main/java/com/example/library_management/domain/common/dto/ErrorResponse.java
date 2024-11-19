package com.example.library_management.domain.common.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NONE // 타입 정보를 사용하지 않음
)
@Getter
@RequiredArgsConstructor
public class ErrorResponse {
    private final int status;
    private final String message;
}
