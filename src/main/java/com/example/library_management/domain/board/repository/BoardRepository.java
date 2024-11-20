package com.example.library_management.domain.board.repository;

import com.example.library_management.domain.board.dto.response.BoardListResponseDto;
import com.example.library_management.domain.board.entity.Board;
import com.example.library_management.domain.board.enums.BoardStatus;
import com.example.library_management.domain.board.enums.BoardType;
import com.example.library_management.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardRepositoryCustom {
//    @Query("SELECT DISTINCT b FROM Board b JOIN FETCH b.user WHERE b.boardType = :boardType AND b.status = :status ORDER BY b.isPinned DESC, b.createdAt DESC")
//    Page<BoardListResponseDto> findByBoardTypeAndStatus(@Param("boardType") BoardType boardType,@Param("status") BoardStatus boardStatus, Pageable pageable);   // 모든 게시글 조회 (관리자용)

    // 모든 게시글 조회 (관리자)
    @Query("SELECT new com.example.library_management.domain.board.dto.response.BoardListResponseDto(" +
            "b.id, b.title, u.userName, b.viewCount, b.isSecret, b.isPinned, b.boardType, b.modifiedAt, " +
            "(SELECT COUNT(c) FROM BoardComment c WHERE c.board = b)) " +
            "FROM Board b " +
            "JOIN b.user u " +
            "WHERE b.boardType = :boardType " +
            "AND b.status = :status " +
            "ORDER BY b.isPinned DESC, b.modifiedAt DESC")
    Page<BoardListResponseDto> findAllBoardDtoList(
            @Param("boardType") BoardType boardType,
            @Param("status") BoardStatus status,
            Pageable pageable
    );

//    @Query("SELECT DISTINCT b FROM Board b JOIN FETCH b.user WHERE b.boardType = :boardType AND b.status = :status AND (b.isSecret = false OR b.user= :user) ORDER BY b.isPinned DESC, b.createdAt DESC")
//    Page<BoardListResponseDto> findByBoardTypeAndStatusAndIsSecretFalseOrUser(@Param("boardType") BoardType boardType, @Param("status") BoardStatus boardStatus, @Param("user") User user, Pageable pageable);  // 공개글 또는 해당 사용자의 게시글 조회(일반 사용자)

    // 공개글 + 본인 게시글 조회 (로그인 사용자)
    @Query("SELECT new com.example.library_management.domain.board.dto.response.BoardListResponseDto(" +
            "b.id, b.title, u.userName, b.viewCount, b.isSecret, b.isPinned, b.boardType, b.modifiedAt, " +
            "(SELECT COUNT(c) FROM BoardComment c WHERE c.board = b)) " +
            "FROM Board b " +
            "JOIN b.user u " +
            "WHERE b.boardType = :boardType " +
            "AND b.status = :status " +
            "AND (b.isSecret = false OR b.user = :user) " +
            "ORDER BY b.isPinned DESC, b.modifiedAt DESC")
    Page<BoardListResponseDto> findBoardDtoListForUser(
            @Param("boardType") BoardType boardType,
            @Param("status") BoardStatus status,
            @Param("user") User user,
            Pageable pageable
    );

//    @Query("SELECT DISTINCT b FROM Board b JOIN FETCH b.user WHERE b.boardType = :boardType AND b.status = :status AND b.isSecret = false order by b.isPinned DESC, b.createdAt DESC")
//    Page<BoardListResponseDto> findByBoardTypeAndStatusAndIsSecretFalse(@Param("boardType") BoardType boardType, @Param("status") BoardStatus boardStatus, Pageable pageable);   // 공개글만 조회

    // 공개글만 조회 (일반)
    @Query("SELECT new com.example.library_management.domain.board.dto.response.BoardListResponseDto(" +
            "b.id, b.title, u.userName, b.viewCount, b.isSecret, b.isPinned, b.boardType, b.modifiedAt, " +
            "(SELECT COUNT(c) FROM BoardComment c WHERE c.board = b)) " +
            "FROM Board b " +
            "JOIN b.user u " +
            "WHERE b.boardType = :boardType " +
            "AND b.status = :status " +
            "AND b.isSecret = false " +
            "ORDER BY b.isPinned DESC, b.modifiedAt DESC")
    Page<BoardListResponseDto> findBoardDtoList(
            @Param("boardType") BoardType boardType,
            @Param("status") BoardStatus status,
            Pageable pageable
    );

    @Query("SELECT DISTINCT b FROM Board b JOIN FETCH b.user LEFT JOIN FETCH b.commentList c LEFT JOIN FETCH c.user WHERE b.id = :boardId")
    Optional<Board> findByIdWithUserAndComments(@Param("boardId") Long boardId);  // 게시물 상세 조회용  ( 댓글 정보 조회 )
}
