package com.example.library_management.domain.boardComment.service;

import com.example.library_management.domain.board.entity.Board;
import com.example.library_management.domain.board.service.BoardService;
import com.example.library_management.domain.boardComment.dto.request.BoardCommentRequestDto;
import com.example.library_management.domain.boardComment.dto.response.BoardCommentResponseDto;
import com.example.library_management.domain.boardComment.entity.BoardComment;
import com.example.library_management.domain.boardComment.exception.CommentMismatchException;
import com.example.library_management.domain.boardComment.exception.CommentNouFoundException;
import com.example.library_management.domain.boardComment.exception.UnauthorizedCommentAccessException;
import com.example.library_management.domain.boardComment.repository.BoardCommentRepository;
import com.example.library_management.domain.user.entity.User;
import com.example.library_management.domain.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardCommentService {
    private final BoardCommentRepository boardCommentRepository;
    private final BoardService boardService;
    
    // 댓글 생성 메서드
    @Transactional
    public BoardCommentResponseDto createComment(Long boardId, BoardCommentRequestDto requestDto, User user) {
        Board board = boardService.findBoardById(boardId);

        BoardComment comment = BoardComment.builder()
                .content(requestDto.getContent())
                .board(board)
                .user(user)
                .build();

        BoardComment saveComment = boardCommentRepository.save(comment);

        return new BoardCommentResponseDto(saveComment);
    }

    @Transactional
    public BoardCommentResponseDto updateComment(Long boardId, Long commentId, BoardCommentRequestDto requestDto, User user) {
        Board board = boardService.findBoardById(boardId);

        BoardComment comment = findCommentById(commentId);

        // 댓글이 해당 게시글의 것인지 확인하는 로직 추가
        if (!comment.getBoard().getId().equals(boardId)) {
            throw new CommentMismatchException();
        }

        // 권한 검증 (작성자 또는 관리자)
        validateCommentAccess(comment, user);

        comment.updateContent(requestDto.getContent());
        boardCommentRepository.save(comment);

        return new BoardCommentResponseDto(comment);
    }
    
    // 게시글의 못든 댓글 조회 메서드
    public List<BoardCommentResponseDto> getCommentList(Long boardId) {
        return boardCommentRepository.findByBoardId(boardId).stream()
                .map(BoardCommentResponseDto::new)
                .collect(Collectors.toList());
    }
    
    // 댓글 삭제 메서드
    @Transactional
    public String deleteComment(Long boardId, Long commentId, User user) {
        BoardComment comment = findCommentById(commentId);

        // 댓글이 해당 게시글의 것인지 확인하는 로직 추가
        if (!comment.getBoard().getId().equals(boardId)) {
            throw new CommentMismatchException();
        }

        // 권한 검증 (작성자 또는 관리자)
        validateCommentAccess(comment, user);

        boardCommentRepository.delete(comment);

        return "댓글 삭제가 완료되었습니다";
    }
    
    // 댓글 소유권 검증 메서드
    private void validateCommentAccess(BoardComment comment, User user) {
        boolean isWriter = comment.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRole().equals(UserRole.ROLE_ADMIN);

        if (!isWriter && !isAdmin) {
            throw new UnauthorizedCommentAccessException();
        }
    }
    
    // 댓글 찾기 메서드
    private BoardComment findCommentById(Long commentId) {
        return boardCommentRepository.findByIdWithUserAndBoard(commentId)
                .orElseThrow(() -> new CommentNouFoundException());
    }
    
}
