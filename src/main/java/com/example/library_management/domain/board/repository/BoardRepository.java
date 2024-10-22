package com.example.library_management.domain.board.repository;

import com.example.library_management.domain.board.entity.Board;
import com.example.library_management.domain.board.enums.BoardStatus;
import com.example.library_management.domain.board.enums.BoardType;
import com.example.library_management.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Long> {
    @Query("SELECT b FROM Board b WHERE b.boardType = :boardType AND b.status = :status ORDER BY b.isPinned DESC, b.createdAt DESC")
    Page<Board> findByBoardTypeAndStatus(@Param("boardType") BoardType boardType,@Param("status") BoardStatus boardStatus, Pageable pageable);   // 모든 게시글 조회 (관리자용)

    @Query("SELECT b FROM Board b WHERE b.boardType = :boardType AND b.status = :status AND (b.isSecret = false OR b.user= :user) ORDER BY b.isPinned DESC, b.createdAt DESC")
    Page<Board> findByBoardTypeAndStatusAndIsSecretFalseOrUser(@Param("boardType") BoardType boardType, @Param("status") BoardStatus boardStatus, @Param("user") User user, Pageable pageable);  // 공개글 또는 해당 사용자의 게시글 조회(일반 사용자)

    @Query("SELECT b FROM Board b WHERE b.boardType = :boardType AND b.status = :status AND b.isSecret = false order by b.isPinned DESC, b.createdAt DESC")
    Page<Board> findByBoardTypeAndStatusAndIsSecretFalse(@Param("boardType") BoardType boardType, @Param("status") BoardStatus boardStatus, Pageable pageable);   // 공개글만 조회
}
