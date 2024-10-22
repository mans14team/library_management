package com.example.library_management.domain.board.controller;

import com.example.library_management.domain.board.dto.request.BoardCreateRequestDto;
import com.example.library_management.domain.board.dto.request.BoardUpdateRequestDto;
import com.example.library_management.domain.board.dto.response.BoardListResponseDto;
import com.example.library_management.domain.board.dto.response.BoardResponseDto;
import com.example.library_management.domain.board.enums.BoardType;
import com.example.library_management.domain.board.service.BoardService;
import com.example.library_management.global.config.ApiResponse;
import com.example.library_management.global.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    /**
     * 게시글 작성 API
     * @param requestDto
     * @param userDetails
     * @return String
     */
    @PostMapping("/board")
    public ResponseEntity<ApiResponse<BoardResponseDto>> createBoard(@Valid @RequestBody BoardCreateRequestDto requestDto,
                                                                     @AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseEntity.ok(ApiResponse.success(boardService.createBoard(requestDto, userDetails.getUser())));
    }

    /**
     * 게시글 상세 조회 API
     * @param boardId
     * @param userDetails
     * @return 게시글 상세 정보
     */
    @GetMapping("/board/{boardId}")
    public ResponseEntity<ApiResponse<BoardResponseDto>> getBoard(@PathVariable Long boardId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        BoardResponseDto responseDto = boardService.getBoard(boardId, userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

    /**
     * 게시글 목록 조회 API
     * @param boardType
     * @param includeSecret
     * @param pageable
     * @param userDetails
     * @return
     */
    @GetMapping("/board")
    public ResponseEntity<ApiResponse<Page<BoardListResponseDto>>> getBoardList(@RequestParam BoardType boardType,
                                                                               @RequestParam(defaultValue = "false") boolean includeSecret,
                                                                               @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable,
                                                                               @AuthenticationPrincipal UserDetailsImpl userDetails){
        Page<BoardListResponseDto> boardList = boardService.getBoardList(boardType, includeSecret, pageable, userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.success(boardList));
    }

    // 게시글 검색 api


    /**
     * 게시글 수정 api( 일부분만 수정될 수 있도록 )
     * @param boardId
     * @param requestDto
     * @param userDetails
     * @return
     */
    @PatchMapping("/board/{boardId}")
    public ResponseEntity<ApiResponse<BoardResponseDto>> updateBoard(@PathVariable Long boardId, @RequestBody BoardUpdateRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        BoardResponseDto responseDto = boardService.updateBoard(boardId, requestDto, userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

    /**
     * 게시글 삭제 API
     * @param boardId
     * @param userDetails
     * @return String
     */
    @DeleteMapping("/board/{boardId}")
    public ResponseEntity<ApiResponse<String>> deleteBoard(@PathVariable Long boardId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseEntity.ok(ApiResponse.success(boardService.deleteBoard(boardId, userDetails.getUser())));
    }
}
