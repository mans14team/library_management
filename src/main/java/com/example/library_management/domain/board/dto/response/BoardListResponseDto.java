package com.example.library_management.domain.board.dto.response;

import com.example.library_management.domain.board.entity.Board;
import com.example.library_management.domain.board.enums.BoardType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class BoardListResponseDto implements Serializable {
    private Long id;
    private String title;
    @JsonProperty("username")
    private String username;        // 작성자 이름
    private int viewCount;
    @JsonProperty("secret")
    private boolean isSecret;
    @JsonProperty("pinned")
    private boolean isPinned;
    private BoardType boardType;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedAt;
    private Long commentCount;       // 댓글 수

    public BoardListResponseDto(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.username = board.getUser().getUserName();
        this.viewCount = board.getViewCount();
        this.isSecret = board.isSecret();
        this.isPinned = board.isPinned();
        this.boardType = board.getBoardType();
        this.modifiedAt = board.getModifiedAt();
        this.commentCount = (long) board.getCommentList().size();
    }

    public BoardListResponseDto(Long id, String title, String username,
                                int viewCount, boolean isSecret, boolean isPinned,
                                BoardType boardType, LocalDateTime modifiedAt,
                                long commentCount) {
        this.id = id;
        this.title = title;
        this.username = username;
        this.viewCount = viewCount;
        this.isSecret = isSecret;
        this.isPinned = isPinned;
        this.boardType = boardType;
        this.modifiedAt = modifiedAt;
        this.commentCount = commentCount;
    }
}
