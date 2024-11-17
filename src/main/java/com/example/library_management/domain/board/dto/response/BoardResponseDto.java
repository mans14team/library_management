package com.example.library_management.domain.board.dto.response;

import com.example.library_management.domain.board.entity.Board;
import com.example.library_management.domain.board.enums.BoardStatus;
import com.example.library_management.domain.board.enums.BoardType;
import com.example.library_management.domain.boardComment.dto.response.BoardCommentResponseDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class BoardResponseDto {
    private Long id;
    private String title;
    private String content;
    private int viewCount;
    @JsonProperty("secret")
    private boolean isSecret;
    @JsonProperty("pinned")
    private boolean isPinned;
    private BoardType boardType;
    private BoardStatus status;
    private String username;        // 작성자 이름
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedAt;
    private List<BoardCommentResponseDto> comments;

    public BoardResponseDto(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.viewCount = board.getViewCount();
        this.isSecret = board.isSecret();
        this.isPinned = board.isPinned();
        this.boardType = board.getBoardType();
        this.status = board.getStatus();
        this.username = board.getUser().getUserName();
        this.createdAt = board.getCreatedAt();
        this.modifiedAt = board.getModifiedAt();
        this.comments = board.getCommentList().stream()
                .map(BoardCommentResponseDto::new)
                .collect(Collectors.toList());
    }
}
