package com.example.library_management.domain.board.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NONE // 타입 정보를 사용하지 않음
)
@Getter
@NoArgsConstructor
public class BoardUpdateRequestDto {
    private String title;
    private String content;
    private Boolean isSecret;
    private Boolean isPinned;
}
