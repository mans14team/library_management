package com.example.library_management.domain.boardComment.entity;

import com.example.library_management.domain.board.entity.Board;
import com.example.library_management.domain.common.entity.Timestamped;
import com.example.library_management.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "board_comment")
public class BoardComment extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public BoardComment(String content, Board board, User user) {
        this.content = content;
        this.board = board;
        this.user = user;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
