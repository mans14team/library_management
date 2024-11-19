package com.example.library_management.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NONE // 타입 정보를 사용하지 않음
)
@Getter
public class UserResponseDto {
    private final Long id;
    private final String email;

    public UserResponseDto(Long id, String email) {
        this.id = id;
        this.email = email;
    }
}
