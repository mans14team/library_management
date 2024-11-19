package com.example.library_management.domain.boardComment.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NONE // 타입 정보를 사용하지 않음
)
@Getter
public class BoardCommentRequestDto {
    @NotBlank(message = "댓글 내용은 필수입니다.")
    private String content;
}
