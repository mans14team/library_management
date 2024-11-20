package com.example.library_management.domain.board.service;

import com.example.library_management.domain.board.dto.request.BoardCreateRequestDto;
import com.example.library_management.domain.board.dto.request.BoardSearchCondition;
import com.example.library_management.domain.board.dto.request.BoardUpdateRequestDto;
import com.example.library_management.domain.board.dto.response.BoardCacheDto;
import com.example.library_management.domain.board.dto.response.BoardListResponseDto;
import com.example.library_management.domain.board.dto.response.BoardResponseDto;
import com.example.library_management.domain.board.dto.response.BoardSearchResult;
import com.example.library_management.domain.board.entity.Board;
import com.example.library_management.domain.board.enums.BoardStatus;
import com.example.library_management.domain.board.enums.BoardType;
import com.example.library_management.domain.board.exception.BoardAuthorityException;
import com.example.library_management.domain.board.exception.BoardNotFoundException;
import com.example.library_management.domain.board.repository.BoardRepository;
import com.example.library_management.domain.user.entity.User;
import com.example.library_management.domain.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardCacheService boardCacheService;
    
    // 게시글 생성
    @Transactional
    @CacheEvict(value = "boardList", allEntries = true)
    public BoardResponseDto createBoard(BoardCreateRequestDto requestDto, User user){
        log.info("Creating new board and evicting boardList cache");

        // 게시글 작성 권한 검증
        validateBoardCreateAuthority(requestDto.getBoardType(), user);
        
        // 게시글 생성
        Board board = new Board(
                requestDto.getTitle(),
                requestDto.getContent(),
                requestDto.isSecret(),
                requestDto.isPinned(),
                requestDto.getBoardType(),
                user
        );

        Board saveBoard = boardRepository.save(board);

        return new BoardResponseDto(saveBoard);
    }

    // 게시글 상세 조회
    @Transactional
    @Cacheable(value = "board",
    key = "#boardId",
    unless = "#result == null")
    public BoardResponseDto getBoard(Long boardId, User user) {
        log.info("Cache Miss - Fetching board detail from DB: boardId={}", boardId);
        Board board = findBoardById(boardId);

        // 게시글이 삭제된 상태인지 확인
        if (board.getStatus() == BoardStatus.INACTIVE) {
            throw new BoardNotFoundException();
        }

        // 비밀글 조회 권한 검증
        validateSecretBoardAccess(board, user);

        board.incrementViewCount();
        boardRepository.save(board);

        return new BoardResponseDto(board);
    }

    // 게시글 목록 조회
    public Page<BoardListResponseDto> getBoardList(BoardType boardType, boolean includeSecret,
                                                   Pageable pageable, User user) {
        BoardCacheDto cacheData = boardCacheService.getCachedBoardList(boardType, includeSecret, pageable, user);

        return new PageImpl<>(cacheData.getContent(), pageable, cacheData.getTotalElements());
    }

    // 게시글 검색 메서드
    public Page<BoardSearchResult> searchBoardList(BoardSearchCondition condition, Pageable pageable, User user) {
        return boardRepository.search(condition, user, pageable);
    }

    // 게시글 수정
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "board", key = "#boardId"),
            @CacheEvict(value = "boardList", allEntries = true)
    })
    public BoardResponseDto updateBoard(Long boardId, BoardUpdateRequestDto requestDto, User user) {
        log.info("Updating board and evicting related caches: boardId={}", boardId);

        Board board = findBoardById(boardId);

        // 삭제된 게시글 체크 추가
        if (board.getStatus() == BoardStatus.INACTIVE) {
            throw new BoardNotFoundException();
        }

        // 수정 권한 검증
        validateUpdateAuthority(board, user);

        // 부분 수정 적용
        board.partialUpdate(requestDto);

        Board saveBoard = boardRepository.save(board);

        return new BoardResponseDto(saveBoard);
    }

    // 게시글 삭제
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "board", key = "#boardId"),
            @CacheEvict(value = "boardList", allEntries = true)
    })
    public String deleteBoard(Long boardId, User user) {
        log.info("Deleting board and evicting related caches: boardId={}", boardId);

        Board board = findBoardById(boardId);

        // 이미 삭제된 게시글인지 확인하는 로직 추가
        if (board.getStatus() == BoardStatus.INACTIVE) {
            throw new BoardNotFoundException();
        }

        // 삭제 권한 검증
        if (!board.getUser().getId().equals(user.getId()) &&
                !user.getRole().equals(UserRole.ROLE_ADMIN)) {
            throw new BoardAuthorityException();
        }

        board.updateStatus(BoardStatus.INACTIVE);
        boardRepository.save(board);

        return "게시글 삭제가 완료되었습니다.";
    }



    // 게시글 작성 권한 검증
    private void validateBoardCreateAuthority(BoardType boardType, User user) {
        if (boardType == BoardType.NOTICE && !user.getRole().equals(UserRole.ROLE_ADMIN)) {
            throw new BoardAuthorityException();
        }

        if (boardType == BoardType.INQUIRY && !user.getRole().equals(UserRole.ROLE_USER)) {
            throw new BoardAuthorityException();
        }
    }

    private void validateSecretBoardAccess(Board board, User user) {
        if (board.isSecret() &&
                !board.getUser().getId().equals(user.getId()) &&
                !user.getRole().equals(UserRole.ROLE_ADMIN)) {
            throw new BoardAuthorityException();
        }
    }

    private void validateUpdateAuthority(Board board, User user) {
        boolean isWriter = board.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRole().equals(UserRole.ROLE_ADMIN);

        if (!isWriter && !isAdmin) {
            throw new BoardAuthorityException();
        }
    }

    public Board findBoardById(Long boardId) {
        return boardRepository.findByIdWithUserAndComments(boardId)
                .orElseThrow(() -> new BoardNotFoundException());
    }
}
