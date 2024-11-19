package com.example.library_management.domain.board.dto.request;

import com.example.library_management.domain.board.enums.BoardType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardCreateRequestDto {
    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    @NotNull(message = "게시판 종류는 필수입니다.")
    private BoardType boardType;

    private boolean isSecret = false;
    private boolean isPinned = false;
}
