package com.example.library_management.domain.boardComment.repository;

import com.example.library_management.domain.boardComment.entity.BoardComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardCommentRepository extends JpaRepository<BoardComment, Long> {
    @Query("SELECT DISTINCT c FROM BoardComment c JOIN FETCH c.user u LEFT JOIN FETCH u.membership WHERE c.board.id = :boardId")
    List<BoardComment> findByBoardId(@Param("boardId") Long boardId);   // 게시글의 모든 댓글을 조회하는 메서드

    @Query("SELECT c FROM BoardComment c JOIN FETCH c.user JOIN FETCH c.board WHERE c.id = :commentId")
    Optional<BoardComment> findByIdWithUserAndBoard(@Param("commentId") Long commentId);  // 댓글 ID로 댓글을 조회하는 메서드 
}
