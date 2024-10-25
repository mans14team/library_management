package com.example.library_management.domain.board.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;
@Getter
public class BoardSearchResult {
    private Long id;
    private String title;
    private String writerName;
    private int viewCount;
    private boolean isSecret;
    private boolean isPinned;
    private LocalDateTime createdAt;
    private int commentCount;

    @QueryProjection
    public BoardSearchResult(Long id, String title, String writerName,
                             int viewCount, boolean isSecret, boolean isPinned,
                             LocalDateTime createdAt, int commentCount) {
        this.id = id;
        this.title = title;
        this.writerName = writerName;
        this.viewCount = viewCount;
        this.isSecret = isSecret;
        this.isPinned = isPinned;
        this.createdAt = createdAt;
        this.commentCount = commentCount;
    }
}
