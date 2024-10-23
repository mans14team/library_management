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
