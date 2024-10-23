package com.example.library_management.domain.boardComment.dto.response;

import com.example.library_management.domain.boardComment.entity.BoardComment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BoardCommentResponseDto {
    private Long id;
    private String content;
    private String username; // 작성자 이름
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public BoardCommentResponseDto(BoardComment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.username = comment.getUser().getUserName();
        this.createdAt = comment.getCreatedAt();
        this.modifiedAt = comment.getModifiedAt();
    }
}
