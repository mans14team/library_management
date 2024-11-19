package com.example.library_management.domain.boardComment.dto.response;

import com.example.library_management.domain.boardComment.entity.BoardComment;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;

import java.time.LocalDateTime;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NONE // 타입 정보를 사용하지 않음
)
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
