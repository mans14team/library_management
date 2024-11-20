package com.example.library_management.domain.board.entity;

import com.example.library_management.domain.board.dto.request.BoardUpdateRequestDto;
import com.example.library_management.domain.board.enums.BoardStatus;
import com.example.library_management.domain.board.enums.BoardType;
import com.example.library_management.domain.boardComment.entity.BoardComment;
import com.example.library_management.domain.common.entity.Timestamped;
import com.example.library_management.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "board", indexes = {
        @Index(name = "idx_board_type_status_pinned_modified",
        columnList = "board_type,status,is_pinned DESC,modified_at DESC"),
        @Index(name = "idx_board_title", columnList = "title")
})
public class Board extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private int viewCount;

    private boolean isSecret;   // 비밀글 여부
    private boolean isPinned;  // 상단 고정 여부

    @Enumerated(EnumType.STRING)
    private BoardType boardType; // NOTICE, INQUIRY
    
    @Enumerated(EnumType.STRING)
    private BoardStatus status;  // 게시글 상태
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 작성자

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<BoardComment> commentList = new ArrayList<>();

    public Board(String title, String content, boolean isSecret, boolean isPinned, BoardType boardType, User user) {
        this.title = title;
        this.content = content;
        this.isSecret = isSecret;
        this.isPinned = isPinned;
        this.boardType = boardType;
        this.user = user;
        this.status = BoardStatus.ACTIVE;
        this.viewCount = 0;
    }

    // 게시글 상태 변경 메서드 (삭제 시 사용)
    public void updateStatus(BoardStatus status) {
        this.status = status;
    }

    // 조회수 증가 메서드
    public void incrementViewCount() {
        this.viewCount++;
    }
    
    // 게시글 수정 메서드
    public void partialUpdate(BoardUpdateRequestDto requestDto) {
        // Optional을 사용하여 null이 아닌 경우에만 업데이트
        Optional.ofNullable(requestDto.getTitle())
                .ifPresent(title -> this.title = title);

        Optional.ofNullable(requestDto.getContent())
                .ifPresent(content -> this.content = content);

        Optional.ofNullable(requestDto.getIsSecret())
                .ifPresent(isSecret -> this.isSecret = isSecret);

        Optional.ofNullable(requestDto.getIsPinned())
                .ifPresent(isPinned -> this.isPinned = isPinned);
    }
    
}
