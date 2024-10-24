package com.example.library_management.domain.boardComment.controller;

import com.example.library_management.domain.boardComment.dto.request.BoardCommentRequestDto;
import com.example.library_management.domain.boardComment.dto.response.BoardCommentResponseDto;
import com.example.library_management.domain.boardComment.service.BoardCommentService;
import com.example.library_management.global.config.ApiResponse;
import com.example.library_management.global.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/library/board/{boardId}")
public class BoardCommentController {
    private final BoardCommentService boardCommentService;
    
    /**
     * 댓글 작성 api
     * @param boardId
     * @param requestDto
     * @param userDetails
     * @return 댓글에 전체적인 내용들
     */
    @PostMapping("/comment")
    public ResponseEntity<ApiResponse<BoardCommentResponseDto>> createComment(@PathVariable Long boardId, @Valid @RequestBody BoardCommentRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseEntity.ok(ApiResponse.success(boardCommentService.createComment(boardId, requestDto, userDetails.getUser())));
    }

    /**
     * 댓글 수정 api
     * @param boardId
     * @param commentId
     * @param requestDto
     * @param userDetails
     * @return 댓글 전체적인 내용들
     */
    @PutMapping("/comment/{commentId}")
    public ResponseEntity<ApiResponse<BoardCommentResponseDto>> updateComment(@PathVariable Long boardId, @PathVariable Long commentId, @RequestBody BoardCommentRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseEntity.ok(ApiResponse.success(boardCommentService.updateComment(boardId, commentId, requestDto, userDetails.getUser())));
    }

    /**
     * 게시글 모든 댓글 조회 api
     * @param boardId
     * @return 댓글 정보 리스트
     */
    @GetMapping("/comment")
    public ResponseEntity<ApiResponse<List<BoardCommentResponseDto>>> getCommentList(@PathVariable Long boardId){
        return ResponseEntity.ok(ApiResponse.success(boardCommentService.getCommentList(boardId)));
    }

    /**
     * 댓글 삭제 api
     * @param boardId
     * @param commentId
     * @param userDetails
     * @return "댓글 삭제가 완료도었습니다."
     */
    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<ApiResponse<String>> deleteComment(@PathVariable Long boardId, @PathVariable Long commentId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseEntity.ok(ApiResponse.success(boardCommentService.deleteComment(boardId, commentId, userDetails.getUser())));
    }
}
