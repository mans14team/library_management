package com.example.library_management.domain.boardComment.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class BoardCommentRequestDto {
    @NotBlank(message = "댓글 내용은 필수입니다.")
    private String content;
}
