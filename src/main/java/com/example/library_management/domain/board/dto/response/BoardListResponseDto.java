package com.example.library_management.domain.board.dto.response;

import com.example.library_management.domain.board.entity.Board;
import com.example.library_management.domain.board.enums.BoardType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BoardListResponseDto {
    private Long id;
    private String title;
    private String username;        // 작성자 이름
    private int viewCount;
    private boolean isSecret;
    private boolean isPinned;
    private BoardType boardType;
    private LocalDateTime modifiedAy;
    private int commentCount;       // 댓글 수

    public BoardListResponseDto(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.username = board.getUser().getUserName();
        this.viewCount = board.getViewCount();
        this.isSecret = board.isSecret();
        this.isPinned = board.isPinned();
        this.boardType = board.getBoardType();
        this.modifiedAy = board.getModifiedAt();
        this.commentCount = board.getCommentList().size();
    }
}
