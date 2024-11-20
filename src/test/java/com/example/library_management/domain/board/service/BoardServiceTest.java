package com.example.library_management.domain.board.service;

import com.example.library_management.domain.board.dto.request.BoardCreateRequestDto;
import com.example.library_management.domain.board.dto.request.BoardSearchCondition;
import com.example.library_management.domain.board.dto.request.BoardUpdateRequestDto;
import com.example.library_management.domain.board.dto.response.BoardCacheDto;
import com.example.library_management.domain.board.dto.response.BoardListResponseDto;
import com.example.library_management.domain.board.dto.response.BoardResponseDto;
import com.example.library_management.domain.board.dto.response.BoardSearchResult;
import com.example.library_management.domain.board.entity.Board;
import com.example.library_management.domain.board.enums.BoardSearchType;
import com.example.library_management.domain.board.enums.BoardStatus;
import com.example.library_management.domain.board.enums.BoardType;
import com.example.library_management.domain.board.exception.BoardAuthorityException;
import com.example.library_management.domain.board.exception.BoardNotFoundException;
import com.example.library_management.domain.board.repository.BoardRepository;
import com.example.library_management.domain.common.exception.GlobalExceptionConst;
import com.example.library_management.domain.user.entity.User;
import com.example.library_management.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {
    @Mock
    private BoardRepository boardRepository;

    @Mock
    private BoardCacheService boardCacheService;

    @InjectMocks
    private BoardService boardService;

    private User user;
    private User admin;
    private Board board;

    @BeforeEach
    void setUp() {
        // 일반 사용자 설정
        user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(user, "role", UserRole.ROLE_USER);
        ReflectionTestUtils.setField(user, "userName", "testUser");

        // 관리자 설정
        admin = new User();
        ReflectionTestUtils.setField(admin, "id", 2L);
        ReflectionTestUtils.setField(admin, "role", UserRole.ROLE_ADMIN);
        ReflectionTestUtils.setField(admin, "userName", "admin");

        boardService = new BoardService(boardRepository, boardCacheService);
    }

    @Nested
    @DisplayName("게시글 생성 테스트")
    class CreateBoardTest{
        @Test
        void 일반_사용자의_문의_게시글_생성_성공(){
            // given
            BoardCreateRequestDto createRequestDto = new BoardCreateRequestDto();
            ReflectionTestUtils.setField(createRequestDto, "title", "테스트 제목");
            ReflectionTestUtils.setField(createRequestDto, "content", "테스트 내용");
            ReflectionTestUtils.setField(createRequestDto, "boardType", BoardType.INQUIRY);
            ReflectionTestUtils.setField(createRequestDto, "isSecret", false);
            ReflectionTestUtils.setField(createRequestDto, "isPinned", false);

            board = new Board(
                    createRequestDto.getTitle(),
                    createRequestDto.getContent(),
                    createRequestDto.isSecret(),
                    createRequestDto.isPinned(),
                    createRequestDto.getBoardType(),
                    user
            );
            ReflectionTestUtils.setField(board, "id", 1L);
            ReflectionTestUtils.setField(board, "viewCount", 0);
            ReflectionTestUtils.setField(board, "status", BoardStatus.ACTIVE);

            given(boardRepository.save(any(Board.class))).willReturn(board);

            // when
            BoardResponseDto responseDto = boardService.createBoard(createRequestDto, user);

            // then
            assertNotNull(responseDto);
            assertEquals("테스트 제목", responseDto.getTitle());
            assertEquals("테스트 내용", responseDto.getContent());
            assertEquals(BoardType.INQUIRY, responseDto.getBoardType());
            assertEquals(BoardStatus.ACTIVE, responseDto.getStatus());
            assertEquals("testUser", responseDto.getUsername());
            assertEquals(0, responseDto.getViewCount());
            assertFalse(responseDto.isSecret());
            assertFalse(responseDto.isPinned());
            verify(boardRepository, times(1)).save(any(Board.class));
        }

        @Test
        void 관리자의_공지사항_생성_성공(){
            // given
            BoardCreateRequestDto createRequestDto = new BoardCreateRequestDto();
            ReflectionTestUtils.setField(createRequestDto, "title", "공지사항");
            ReflectionTestUtils.setField(createRequestDto, "content", "공지사항 내용");
            ReflectionTestUtils.setField(createRequestDto, "boardType", BoardType.NOTICE);
            ReflectionTestUtils.setField(createRequestDto, "isPinned", true);
            ReflectionTestUtils.setField(createRequestDto, "isSecret", false);

            Board board = new Board(
                    createRequestDto.getTitle(),
                    createRequestDto.getContent(),
                    createRequestDto.isSecret(),
                    createRequestDto.isPinned(),
                    createRequestDto.getBoardType(),
                    admin
            );
            ReflectionTestUtils.setField(board, "id", 2L);
            ReflectionTestUtils.setField(board, "viewCount", 0);
            ReflectionTestUtils.setField(board, "status", BoardStatus.ACTIVE);

            given(boardRepository.save(any(Board.class))).willReturn(board);

            // when
            BoardResponseDto responseDto = boardService.createBoard(createRequestDto, admin);

            // then
            assertNotNull(responseDto);
            assertEquals("공지사항", responseDto.getTitle());
            assertEquals("공지사항 내용", responseDto.getContent());
            assertEquals(BoardType.NOTICE, responseDto.getBoardType());
            assertEquals(BoardStatus.ACTIVE, responseDto.getStatus());
            assertEquals("admin", responseDto.getUsername());
            assertTrue(responseDto.isPinned());
            assertFalse(responseDto.isSecret());
            verify(boardRepository, times(1)).save(any(Board.class));
        }

        @Test
        void 일반_사용자의_공지사항_생성_실패(){
            // given
            BoardCreateRequestDto createRequestDto = new BoardCreateRequestDto();
            ReflectionTestUtils.setField(createRequestDto, "title", "공지사항");
            ReflectionTestUtils.setField(createRequestDto, "content", "공지사항 내용");
            ReflectionTestUtils.setField(createRequestDto, "boardType", BoardType.NOTICE);
            ReflectionTestUtils.setField(createRequestDto, "isPinned", false);
            ReflectionTestUtils.setField(createRequestDto, "isSecret", false);

            // when & then
            BoardAuthorityException exception = assertThrows(BoardAuthorityException.class, () ->
                    boardService.createBoard(createRequestDto, user)
            );

            assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatus());
            assertTrue(exception.getMessage().contains(GlobalExceptionConst.FORBIDDEN_CREATE.getMessage()));
            verify(boardRepository, never()).save(any(Board.class));
        }

        @Test
        void 관리자의_문의사항_생성_실패(){
            // given
            BoardCreateRequestDto createRequestDto = new BoardCreateRequestDto();
            ReflectionTestUtils.setField(createRequestDto, "title", "문의사항");
            ReflectionTestUtils.setField(createRequestDto, "content", "문의사항 내용");
            ReflectionTestUtils.setField(createRequestDto, "boardType", BoardType.INQUIRY);
            ReflectionTestUtils.setField(createRequestDto, "isSecret", false);
            ReflectionTestUtils.setField(createRequestDto, "isPinned", false);

            // when & then
            BoardAuthorityException exception = assertThrows(BoardAuthorityException.class, () ->
                    boardService.createBoard(createRequestDto, admin)
            );

            assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatus());
            assertTrue(exception.getMessage().contains(GlobalExceptionConst.FORBIDDEN_CREATE.getMessage()));
            verify(boardRepository, never()).save(any(Board.class));
        }

        @Test
        void 비밀글_생성_성공(){
            // given
            BoardCreateRequestDto createRequestDto = new BoardCreateRequestDto();
            ReflectionTestUtils.setField(createRequestDto, "title", "비밀글");
            ReflectionTestUtils.setField(createRequestDto, "content", "비밀글 내용");
            ReflectionTestUtils.setField(createRequestDto, "boardType", BoardType.INQUIRY);
            ReflectionTestUtils.setField(createRequestDto, "isSecret", true);
            ReflectionTestUtils.setField(createRequestDto, "isPinned", false);

            Board board = new Board(
                    createRequestDto.getTitle(),
                    createRequestDto.getContent(),
                    createRequestDto.isSecret(),
                    createRequestDto.isPinned(),
                    createRequestDto.getBoardType(),
                    user
            );
            ReflectionTestUtils.setField(board, "id", 3L);
            ReflectionTestUtils.setField(board, "viewCount", 0);
            ReflectionTestUtils.setField(board, "status", BoardStatus.ACTIVE);

            given(boardRepository.save(any(Board.class))).willReturn(board);

            // when
            BoardResponseDto responseDto = boardService.createBoard(createRequestDto, user);

            // then
            assertNotNull(responseDto);
            assertEquals("비밀글", responseDto.getTitle());
            assertEquals("비밀글 내용", responseDto.getContent());
            assertEquals(BoardType.INQUIRY, responseDto.getBoardType());
            assertEquals(BoardStatus.ACTIVE, responseDto.getStatus());
            assertEquals("testUser", responseDto.getUsername());
            assertTrue(responseDto.isSecret());
            assertFalse(responseDto.isPinned());
            verify(boardRepository, times(1)).save(any(Board.class));
        }
    }

    @Nested
    @DisplayName("게시글 상세 조회 테스트")
    class getBoardTest{
        @Test
        void 공개_게시글_조회_성공(){
            // given
            // 공개 게시글 설정
            Board publicBoard = new Board(
                    "공개 게시글",
                    "공개 게시글 내용",
                    false,
                    false,
                    BoardType.INQUIRY,
                    user
            );
            ReflectionTestUtils.setField(publicBoard, "id", 1L);
            ReflectionTestUtils.setField(publicBoard, "viewCount", 0);
            ReflectionTestUtils.setField(publicBoard, "status", BoardStatus.ACTIVE);

            given(boardRepository.findByIdWithUserAndComments(anyLong()))
                    .willReturn(Optional.of(publicBoard));
            given(boardRepository.save(any(Board.class))).willReturn(publicBoard);

            // when
            BoardResponseDto responseDto = boardService.getBoard(1L, user);

            // then
            assertNotNull(responseDto);
            assertEquals("공개 게시글", responseDto.getTitle());
            assertEquals("공개 게시글 내용", responseDto.getContent());
            assertEquals(1, responseDto.getViewCount());  // 조회수 증가 확인
            assertEquals("testUser", responseDto.getUsername());
            assertFalse(responseDto.isSecret());
            verify(boardRepository, times(1)).findByIdWithUserAndComments(anyLong());
            verify(boardRepository, times(1)).save(any(Board.class));
        }

        @Test
        void 작성자의_비밀_게시글_조회_성공(){
            // given
            // 비밀 게시글 설정
            Board secretBoard = new Board(
                    "비밀 게시글",
                    "비밀 게시글 내용",
                    true,
                    false,
                    BoardType.INQUIRY,
                    user
            );
            ReflectionTestUtils.setField(secretBoard, "id", 2L);
            ReflectionTestUtils.setField(secretBoard, "viewCount", 0);
            ReflectionTestUtils.setField(secretBoard, "status", BoardStatus.ACTIVE);

            given(boardRepository.findByIdWithUserAndComments(anyLong()))
                    .willReturn(Optional.of(secretBoard));
            given(boardRepository.save(any(Board.class))).willReturn(secretBoard);

            // when
            BoardResponseDto responseDto = boardService.getBoard(2L, user);

            // then
            assertNotNull(responseDto);
            assertEquals("비밀 게시글", responseDto.getTitle());
            assertEquals("비밀 게시글 내용", responseDto.getContent());
            assertEquals(1, responseDto.getViewCount());
            assertTrue(responseDto.isSecret());
            assertEquals("testUser", responseDto.getUsername());
            verify(boardRepository, times(1)).save(any(Board.class));
        }

        @Test
        void 관리자의_비밀_게시글_조회_성공(){
            // given
            // 비밀 게시글 설정
            Board secretBoard = new Board(
                    "비밀 게시글",
                    "비밀 게시글 내용",
                    true,
                    false,
                    BoardType.INQUIRY,
                    user  // 일반 사용자가 작성한 비밀글
            );
            ReflectionTestUtils.setField(secretBoard, "id", 2L);
            ReflectionTestUtils.setField(secretBoard, "viewCount", 0);
            ReflectionTestUtils.setField(secretBoard, "status", BoardStatus.ACTIVE);

            given(boardRepository.findByIdWithUserAndComments(anyLong()))
                    .willReturn(Optional.of(secretBoard));
            given(boardRepository.save(any(Board.class))).willReturn(secretBoard);

            // when
            BoardResponseDto responseDto = boardService.getBoard(2L, admin);  // 관리자가 조회

            // then
            assertNotNull(responseDto);
            assertEquals("비밀 게시글", responseDto.getTitle());
            assertTrue(responseDto.isSecret());
            assertEquals(1, responseDto.getViewCount());
            verify(boardRepository, times(1)).save(any(Board.class));
        }

        @Test
        void 타인의_비밀_게시글_조회_실패(){
            // given
            // 비밀 게시글 설정
            Board secretBoard = new Board(
                    "비밀 게시글",
                    "비밀 게시글 내용",
                    true,
                    false,
                    BoardType.INQUIRY,
                    user  // user가 작성한 비밀글
            );
            ReflectionTestUtils.setField(secretBoard, "id", 2L);
            ReflectionTestUtils.setField(secretBoard, "viewCount", 0);
            ReflectionTestUtils.setField(secretBoard, "status", BoardStatus.ACTIVE);

            // otherUser 설정
            User otherUser = new User();
            ReflectionTestUtils.setField(otherUser, "id", 3L);
            ReflectionTestUtils.setField(otherUser, "role", UserRole.ROLE_USER);
            ReflectionTestUtils.setField(otherUser, "userName", "otherUser");

            given(boardRepository.findByIdWithUserAndComments(anyLong()))
                    .willReturn(Optional.of(secretBoard));

            // when & then
            assertThrows(BoardAuthorityException.class, () ->
                    boardService.getBoard(2L, otherUser)
            );
            verify(boardRepository, never()).save(any(Board.class));
        }

        @Test
        void 존재하지_않는_게시글_조회_실패() {
            // given
            given(boardRepository.findByIdWithUserAndComments(anyLong()))
                    .willReturn(Optional.empty());

            // when & then
            assertThrows(BoardNotFoundException.class, () ->
                    boardService.getBoard(999L, user)
            );
            verify(boardRepository, never()).save(any(Board.class));
        }

        @Test
        void 삭제된_게시글_조회_실패() {
            // given
            Board deletedBoard = new Board(
                    "삭제된 게시글",
                    "삭제된 게시글 내용",
                    false,
                    false,
                    BoardType.INQUIRY,
                    user
            );
            ReflectionTestUtils.setField(deletedBoard, "id", 3L);
            ReflectionTestUtils.setField(deletedBoard, "viewCount", 0);
            ReflectionTestUtils.setField(deletedBoard, "status", BoardStatus.INACTIVE);

            given(boardRepository.findByIdWithUserAndComments(anyLong()))
                    .willReturn(Optional.of(deletedBoard));

            // when & then
            BoardNotFoundException exception = assertThrows(BoardNotFoundException.class, () ->
                    boardService.getBoard(3L, user)
            );

            // 추가적인 검증
            verify(boardRepository, times(1)).findByIdWithUserAndComments(anyLong());
            verify(boardRepository, never()).save(any(Board.class));
        }

        @Test
        @DisplayName("조회수 증가 확인")
        void 조회수_증가_확인() {
            // given
            Board publicBoard = new Board(
                    "조회수 테스트 게시글",
                    "조회수 테스트 내용",
                    false,
                    false,
                    BoardType.INQUIRY,
                    user
            );
            ReflectionTestUtils.setField(publicBoard, "id", 1L);
            ReflectionTestUtils.setField(publicBoard, "viewCount", 0);
            ReflectionTestUtils.setField(publicBoard, "status", BoardStatus.ACTIVE);

            given(boardRepository.findByIdWithUserAndComments(anyLong()))
                    .willReturn(Optional.of(publicBoard));
            given(boardRepository.save(any(Board.class))).willReturn(publicBoard);

            // when
            int initialViewCount = publicBoard.getViewCount();
            boardService.getBoard(1L, user);

            // then
            assertEquals(initialViewCount + 1, publicBoard.getViewCount());
            verify(boardRepository, times(1)).save(any(Board.class));
        }
    }

    @Nested
    @DisplayName("게시글 목록 조회 테스트")
    class getBoardListTest{
        @Test
        void 관리자의_전체_게시글_목록_조회_성공(){
            // given
            Pageable pageable = PageRequest.of(0, 10);
            List<Board> boards = createSampleBoards();
            List<BoardListResponseDto> dtoList = boards.stream()
                    .map(board -> new BoardListResponseDto(
                            board.getId(),
                            board.getTitle(),
                            board.getUser().getUserName(),
                            board.getViewCount(),
                            board.isSecret(),
                            board.isPinned(),
                            board.getBoardType(),
                            board.getModifiedAt(),
                            0L
                    ))
                    .collect(Collectors.toList());

            BoardCacheDto cacheDto = new BoardCacheDto(dtoList, boards.size());

            given(boardCacheService.getCachedBoardList(any(BoardType.class), anyBoolean(), any(Pageable.class), eq(admin)))
                    .willReturn(cacheDto);

            // when
            Page<BoardListResponseDto> result = boardService.getBoardList(BoardType.INQUIRY, true, pageable, admin);

            // then
            assertNotNull(result);
            assertEquals(4, result.getContent().size());
            verify(boardCacheService).getCachedBoardList(eq(BoardType.INQUIRY), eq(true), eq(pageable), eq(admin));
            verifyNoInteractions(boardRepository);  // Repository는 호출되지 않아야 함
        }

        @Test
        void 로그인_사용자의_게시글_목록_조회_성공_비밀글_포함(){
            // given
            Pageable pageable = PageRequest.of(0, 10);
            List<Board> boards = createSampleBoards().stream()
                    .filter(board -> !board.isSecret() || board.getUser().equals(user))
                    .collect(Collectors.toList());

            List<BoardListResponseDto> dtoList = boards.stream()
                    .map(board -> new BoardListResponseDto(
                            board.getId(),
                            board.getTitle(),
                            board.getUser().getUserName(),
                            board.getViewCount(),
                            board.isSecret(),
                            board.isPinned(),
                            board.getBoardType(),
                            board.getModifiedAt(),
                            0L
                    ))
                    .collect(Collectors.toList());

            BoardCacheDto cacheDto = new BoardCacheDto(dtoList, boards.size());

            given(boardCacheService.getCachedBoardList(any(BoardType.class), anyBoolean(), any(Pageable.class), eq(user)))
                    .willReturn(cacheDto);

            // when
            Page<BoardListResponseDto> result = boardService.getBoardList(BoardType.INQUIRY, true, pageable, user);

            // then
            assertNotNull(result);
            assertEquals(3, result.getContent().size());
            verify(boardCacheService).getCachedBoardList(eq(BoardType.INQUIRY), eq(true), eq(pageable), eq(user));
            verifyNoInteractions(boardRepository);
        }

        @Test
        void 공개_게시글만_목록_조회_성공(){
            // given
            Pageable pageable = PageRequest.of(0, 10);
            List<Board> boards = createSampleBoards().stream()
                    .filter(board -> !board.isSecret())
                    .collect(Collectors.toList());

            List<BoardListResponseDto> dtoList = boards.stream()
                    .map(board -> new BoardListResponseDto(
                            board.getId(),
                            board.getTitle(),
                            board.getUser().getUserName(),
                            board.getViewCount(),
                            board.isSecret(),
                            board.isPinned(),
                            board.getBoardType(),
                            board.getModifiedAt(),
                            0L
                    ))
                    .collect(Collectors.toList());

            BoardCacheDto cacheDto = new BoardCacheDto(dtoList, boards.size());

            given(boardCacheService.getCachedBoardList(any(BoardType.class), anyBoolean(), any(Pageable.class), eq(user)))
                    .willReturn(cacheDto);

            // when
            Page<BoardListResponseDto> result = boardService.getBoardList(BoardType.INQUIRY, false, pageable, user);

            // then
            assertNotNull(result);
            assertEquals(2, result.getContent().size());
            assertTrue(result.getContent().stream().noneMatch(BoardListResponseDto::isSecret));
            verify(boardCacheService).getCachedBoardList(eq(BoardType.INQUIRY), eq(false), eq(pageable), eq(user));
            verifyNoInteractions(boardRepository);
        }

        // 테스트용 샘플 게시글 생성 메서드
        private List<Board> createSampleBoards() {
            List<Board> boards = new ArrayList<>();

            // 일반 사용자의 공개 게시글
            Board publicBoard1 = new Board(
                    "공개 게시글 1",
                    "내용 1",
                    false,
                    false,
                    BoardType.INQUIRY,
                    user
            );
            ReflectionTestUtils.setField(publicBoard1, "id", 1L);
            ReflectionTestUtils.setField(publicBoard1, "viewCount", 10);
            ReflectionTestUtils.setField(publicBoard1, "status", BoardStatus.ACTIVE);

            // 다른 사용자의 공개 게시글
            User otherUser = new User();
            ReflectionTestUtils.setField(otherUser, "id", 3L);
            ReflectionTestUtils.setField(otherUser, "userName", "otherUser");
            ReflectionTestUtils.setField(otherUser, "role", UserRole.ROLE_USER);

            Board publicBoard2 = new Board(
                    "공개 게시글 2",
                    "내용 2",
                    false,
                    true,  // 상단 고정
                    BoardType.INQUIRY,
                    otherUser
            );
            ReflectionTestUtils.setField(publicBoard2, "id", 2L);
            ReflectionTestUtils.setField(publicBoard2, "viewCount", 20);
            ReflectionTestUtils.setField(publicBoard2, "status", BoardStatus.ACTIVE);

            // 일반 사용자의 비밀 게시글
            Board secretBoard1 = new Board(
                    "비밀 게시글 1",
                    "내용 3",
                    true,
                    false,
                    BoardType.INQUIRY,
                    user
            );
            ReflectionTestUtils.setField(secretBoard1, "id", 3L);
            ReflectionTestUtils.setField(secretBoard1, "viewCount", 5);
            ReflectionTestUtils.setField(secretBoard1, "status", BoardStatus.ACTIVE);

            // 다른 사용자의 비밀 게시글
            Board secretBoard2 = new Board(
                    "비밀 게시글 2",
                    "내용 4",
                    true,
                    false,
                    BoardType.INQUIRY,
                    otherUser
            );
            ReflectionTestUtils.setField(secretBoard2, "id", 4L);
            ReflectionTestUtils.setField(secretBoard2, "viewCount", 15);
            ReflectionTestUtils.setField(secretBoard2, "status", BoardStatus.ACTIVE);

            boards.add(publicBoard1);
            boards.add(publicBoard2);
            boards.add(secretBoard1);
            boards.add(secretBoard2);

            return boards;
        }
    }

    @Nested
    @DisplayName("게시글 검색 테스트")
    class searchBoardListTest{
        @Test
        void 관리자의_제목_검색_성공(){
            // given
            BoardSearchCondition condition = createSearchCondition(
                    "테스트",
                    BoardSearchType.TITLE,
                    BoardType.INQUIRY,
                    true
            );
            Pageable pageable = PageRequest.of(0, 10);
            List<Board> boards = createSampleBoards();
            Page<BoardSearchResult> searchResultPage = createSearchResultPage(boards, pageable);

            given(boardRepository.search(any(BoardSearchCondition.class), eq(admin), any(Pageable.class)))
                    .willReturn(searchResultPage);

            // when
            Page<BoardSearchResult> result = boardService.searchBoardList(condition, pageable, admin);

            // then
            assertNotNull(result);
            assertEquals(4, result.getContent().size());
            verify(boardRepository).search(any(BoardSearchCondition.class), eq(admin), any(Pageable.class));
        }

        @Test
        void 일반_사용자의_제목_검색_성공_비밀글_포함(){
            // given
            BoardSearchCondition condition = createSearchCondition(
                    "테스트",
                    BoardSearchType.TITLE,
                    BoardType.INQUIRY,
                    true
            );
            Pageable pageable = PageRequest.of(0, 10);
            List<Board> boards = createSampleBoards().stream()
                    .filter(board -> !board.isSecret() || board.getUser().equals(user))
                    .collect(Collectors.toList());
            Page<BoardSearchResult> searchResultPage = createSearchResultPage(boards, pageable);

            given(boardRepository.search(any(BoardSearchCondition.class), eq(user), any(Pageable.class)))
                    .willReturn(searchResultPage);

            // when
            Page<BoardSearchResult> result = boardService.searchBoardList(condition, pageable, user);

            // then
            assertNotNull(result);
            assertEquals(3, result.getContent().size());  // 공개글 2개 + 자신의 비밀글 1개
            verify(boardRepository).search(any(BoardSearchCondition.class), eq(user), any(Pageable.class));
        }

        @Test
        void 일반_사용자의_작성자_검색_성공_공객글만(){
            // given
            BoardSearchCondition condition = createSearchCondition(
                    "testUser",
                    BoardSearchType.WRITER,
                    BoardType.INQUIRY,
                    false
            );
            Pageable pageable = PageRequest.of(0, 10);
            List<Board> boards = createSampleBoards().stream()
                    .filter(board -> !board.isSecret() && board.getUser().getUserName().equals("testUser"))
                    .collect(Collectors.toList());
            Page<BoardSearchResult> searchResultPage = createSearchResultPage(boards, pageable);

            given(boardRepository.search(any(BoardSearchCondition.class), eq(user), any(Pageable.class)))
                    .willReturn(searchResultPage);

            // when
            Page<BoardSearchResult> result = boardService.searchBoardList(condition, pageable, user);

            // then
            assertNotNull(result);
            assertEquals(1, result.getContent().size());  // 작성자의 공개글만
            assertTrue(result.getContent().stream().noneMatch(BoardSearchResult::isSecret));
            verify(boardRepository).search(any(BoardSearchCondition.class), eq(user), any(Pageable.class));
        }

        @Test
        void 내용_검색_성공(){
            // given
            BoardSearchCondition condition = createSearchCondition(
                    "테스트 내용",
                    BoardSearchType.CONTENT,
                    BoardType.INQUIRY,
                    false
            );
            Pageable pageable = PageRequest.of(0, 10);
            List<Board> boards = createSampleBoards().stream()
                    .filter(board -> !board.isSecret())
                    .collect(Collectors.toList());
            Page<BoardSearchResult> searchResultPage = createSearchResultPage(boards, pageable);

            given(boardRepository.search(any(BoardSearchCondition.class), eq(user), any(Pageable.class)))
                    .willReturn(searchResultPage);

            // when
            Page<BoardSearchResult> result = boardService.searchBoardList(condition, pageable, user);

            // then
            assertNotNull(result);
            assertEquals(2, result.getContent().size());  // 공개글만
            verify(boardRepository).search(any(BoardSearchCondition.class), eq(user), any(Pageable.class));
        }

        @Test
        void 전체_검색_성공(){
            // given
            BoardSearchCondition condition = createSearchCondition(
                    "테스트",
                    BoardSearchType.ALL,
                    BoardType.INQUIRY,
                    false
            );
            Pageable pageable = PageRequest.of(0, 10);
            List<Board> boards = createSampleBoards().stream()
                    .filter(board -> !board.isSecret())
                    .collect(Collectors.toList());
            Page<BoardSearchResult> searchResultPage = createSearchResultPage(boards, pageable);

            given(boardRepository.search(any(BoardSearchCondition.class), eq(user), any(Pageable.class)))
                    .willReturn(searchResultPage);

            // when
            Page<BoardSearchResult> result = boardService.searchBoardList(condition, pageable, user);

            // then
            assertNotNull(result);
            assertEquals(2, result.getContent().size());
            verify(boardRepository).search(any(BoardSearchCondition.class), eq(user), any(Pageable.class));
        }

        // 검색 조건 생성 헬퍼 메서드
        private BoardSearchCondition createSearchCondition(
                String keyword,
                BoardSearchType searchType,
                BoardType boardType,
                boolean includeSecret
        ) {
            BoardSearchCondition condition = new BoardSearchCondition();
            ReflectionTestUtils.setField(condition, "keyword", keyword);
            ReflectionTestUtils.setField(condition, "searchType", searchType);
            ReflectionTestUtils.setField(condition, "boardType", boardType);
            ReflectionTestUtils.setField(condition, "includeSecret", includeSecret);
            return condition;
        }

        // 검색 결과 페이지 생성 헬퍼 메서드
        private Page<BoardSearchResult> createSearchResultPage(List<Board> boards, Pageable pageable) {
            List<BoardSearchResult> searchResults = boards.stream()
                    .map(board -> new BoardSearchResult(
                            board.getId(),
                            board.getTitle(),
                            board.getUser().getUserName(),
                            board.getViewCount(),
                            board.isSecret(),
                            board.isPinned(),
                            board.getCreatedAt(),
                            0  // 댓글 수
                    ))
                    .collect(Collectors.toList());

            return new PageImpl<>(searchResults, pageable, searchResults.size());
        }

        // 테스트용 샘플 게시글 생성 메서드
        private List<Board> createSampleBoards() {
            List<Board> boards = new ArrayList<>();

            // 일반 사용자의 공개 게시글
            Board publicBoard1 = new Board(
                    "테스트 게시글 1",
                    "테스트 내용 1",
                    false,
                    false,
                    BoardType.INQUIRY,
                    user
            );
            ReflectionTestUtils.setField(publicBoard1, "id", 1L);
            ReflectionTestUtils.setField(publicBoard1, "viewCount", 10);
            ReflectionTestUtils.setField(publicBoard1, "status", BoardStatus.ACTIVE);

            // 다른 사용자의 공개 게시글
            User otherUser = new User();
            ReflectionTestUtils.setField(otherUser, "id", 3L);
            ReflectionTestUtils.setField(otherUser, "userName", "otherUser");
            ReflectionTestUtils.setField(otherUser, "role", UserRole.ROLE_USER);

            Board publicBoard2 = new Board(
                    "테스트 게시글 2",
                    "테스트 내용 2",
                    false,
                    true,
                    BoardType.INQUIRY,
                    otherUser
            );
            ReflectionTestUtils.setField(publicBoard2, "id", 2L);
            ReflectionTestUtils.setField(publicBoard2, "viewCount", 20);
            ReflectionTestUtils.setField(publicBoard2, "status", BoardStatus.ACTIVE);

            // 일반 사용자의 비밀 게시글
            Board secretBoard1 = new Board(
                    "비밀 테스트 1",
                    "비밀 내용 1",
                    true,
                    false,
                    BoardType.INQUIRY,
                    user
            );
            ReflectionTestUtils.setField(secretBoard1, "id", 3L);
            ReflectionTestUtils.setField(secretBoard1, "viewCount", 5);
            ReflectionTestUtils.setField(secretBoard1, "status", BoardStatus.ACTIVE);

            // 다른 사용자의 비밀 게시글
            Board secretBoard2 = new Board(
                    "비밀 테스트 2",
                    "비밀 내용 2",
                    true,
                    false,
                    BoardType.INQUIRY,
                    otherUser
            );
            ReflectionTestUtils.setField(secretBoard2, "id", 4L);
            ReflectionTestUtils.setField(secretBoard2, "viewCount", 15);
            ReflectionTestUtils.setField(secretBoard2, "status", BoardStatus.ACTIVE);

            boards.add(publicBoard1);
            boards.add(publicBoard2);
            boards.add(secretBoard1);
            boards.add(secretBoard2);

            return boards;
        }
    }

    @Nested
    @DisplayName("게시글 수정 테스트")
    class updateBoardTest{
        @Test
        void 작성자의_게시글_전체_수정_성공(){
            // given
            Board board = new Board(
                    "원본 제목",
                    "원본 내용",
                    false,
                    false,
                    BoardType.INQUIRY,
                    user
            );
            ReflectionTestUtils.setField(board, "id", 1L);
            ReflectionTestUtils.setField(board, "status", BoardStatus.ACTIVE);

            BoardUpdateRequestDto updateRequest = new BoardUpdateRequestDto();
            ReflectionTestUtils.setField(updateRequest, "title", "수정된 제목");
            ReflectionTestUtils.setField(updateRequest, "content", "수정된 내용");
            ReflectionTestUtils.setField(updateRequest, "isSecret", true);
            ReflectionTestUtils.setField(updateRequest, "isPinned", true);

            given(boardRepository.findByIdWithUserAndComments(anyLong()))
                    .willReturn(Optional.of(board));
            given(boardRepository.save(any(Board.class))).willReturn(board);

            // when
            BoardResponseDto responseDto = boardService.updateBoard(1L, updateRequest, user);

            // then
            assertNotNull(responseDto);
            assertEquals("수정된 제목", responseDto.getTitle());
            assertEquals("수정된 내용", responseDto.getContent());
            assertTrue(responseDto.isSecret());
            assertTrue(responseDto.isPinned());
            assertEquals("testUser", responseDto.getUsername());
            verify(boardRepository).findByIdWithUserAndComments(1L);
            verify(boardRepository).save(any(Board.class));
        }

        @Test
        void 관리자의_게시글_수정_성공(){
            // given
            Board board = new Board(
                    "원본 제목",
                    "원본 내용",
                    false,
                    false,
                    BoardType.INQUIRY,
                    user  // 일반 사용자가 작성한 게시글
            );
            ReflectionTestUtils.setField(board, "id", 1L);
            ReflectionTestUtils.setField(board, "status", BoardStatus.ACTIVE);

            BoardUpdateRequestDto updateRequest = new BoardUpdateRequestDto();
            ReflectionTestUtils.setField(updateRequest, "title", "관리자가 수정한 제목");
            ReflectionTestUtils.setField(updateRequest, "content", "관리자가 수정한 내용");

            given(boardRepository.findByIdWithUserAndComments(anyLong()))
                    .willReturn(Optional.of(board));
            given(boardRepository.save(any(Board.class))).willReturn(board);

            // when
            BoardResponseDto responseDto = boardService.updateBoard(1L, updateRequest, admin);

            // then
            assertNotNull(responseDto);
            assertEquals("관리자가 수정한 제목", responseDto.getTitle());
            assertEquals("관리자가 수정한 내용", responseDto.getContent());
            assertEquals("testUser", responseDto.getUsername());  // 작성자는 변경되지 않음
            verify(boardRepository).save(any(Board.class));
        }

        @Test
        void 게시글_부분_수정_성공(){
            // given
            Board board = new Board(
                    "원본 제목",
                    "원본 내용",
                    false,
                    false,
                    BoardType.INQUIRY,
                    user
            );
            ReflectionTestUtils.setField(board, "id", 1L);
            ReflectionTestUtils.setField(board, "status", BoardStatus.ACTIVE);

            BoardUpdateRequestDto updateRequest = new BoardUpdateRequestDto();
            ReflectionTestUtils.setField(updateRequest, "title", "수정된 제목");
            // content는 null로 두어 수정하지 않음

            given(boardRepository.findByIdWithUserAndComments(anyLong()))
                    .willReturn(Optional.of(board));
            given(boardRepository.save(any(Board.class))).willReturn(board);

            // when
            BoardResponseDto responseDto = boardService.updateBoard(1L, updateRequest, user);

            // then
            assertNotNull(responseDto);
            assertEquals("수정된 제목", responseDto.getTitle());
            assertEquals("원본 내용", responseDto.getContent());  // 내용은 변경되지 않음
            verify(boardRepository).save(any(Board.class));
        }

        @Test
        void 작성자가_아닌_사용자의_게시글_수정_실패(){
            // given
            Board board = new Board(
                    "원본 제목",
                    "원본 내용",
                    false,
                    false,
                    BoardType.INQUIRY,
                    user
            );
            ReflectionTestUtils.setField(board, "id", 1L);
            ReflectionTestUtils.setField(board, "status", BoardStatus.ACTIVE);

            User otherUser = new User();
            ReflectionTestUtils.setField(otherUser, "id", 3L);
            ReflectionTestUtils.setField(otherUser, "role", UserRole.ROLE_USER);
            ReflectionTestUtils.setField(otherUser, "userName", "otherUser");

            BoardUpdateRequestDto updateRequest = new BoardUpdateRequestDto();
            ReflectionTestUtils.setField(updateRequest, "title", "수정된 제목");
            ReflectionTestUtils.setField(updateRequest, "content", "수정된 내용");

            given(boardRepository.findByIdWithUserAndComments(anyLong()))
                    .willReturn(Optional.of(board));

            // when & then
            assertThrows(BoardAuthorityException.class, () ->
                    boardService.updateBoard(1L, updateRequest, otherUser)
            );
            verify(boardRepository, never()).save(any(Board.class));
        }

        @Test
        void 존재하지_않는_게시글_수정_실패(){
            // given
            BoardUpdateRequestDto updateRequest = new BoardUpdateRequestDto();
            ReflectionTestUtils.setField(updateRequest, "title", "수정된 제목");
            ReflectionTestUtils.setField(updateRequest, "content", "수정된 내용");

            given(boardRepository.findByIdWithUserAndComments(anyLong()))
                    .willReturn(Optional.empty());

            // when & then
            assertThrows(BoardNotFoundException.class, () ->
                    boardService.updateBoard(999L, updateRequest, user)
            );
            verify(boardRepository, never()).save(any(Board.class));
        }

        @Test
        void 삭제된_게시글_수정_실패(){
            // given
            Board deletedBoard = new Board(
                    "삭제된 게시글",
                    "삭제된 내용",
                    false,
                    false,
                    BoardType.INQUIRY,
                    user
            );
            ReflectionTestUtils.setField(deletedBoard, "id", 1L);
            ReflectionTestUtils.setField(deletedBoard, "viewCount", 0);  // viewCount 추가
            ReflectionTestUtils.setField(deletedBoard, "status", BoardStatus.INACTIVE);

            BoardUpdateRequestDto updateRequest = new BoardUpdateRequestDto();
            ReflectionTestUtils.setField(updateRequest, "title", "수정된 제목");
            ReflectionTestUtils.setField(updateRequest, "content", null);  // content 명시적으로 설정
            ReflectionTestUtils.setField(updateRequest, "isSecret", null);
            ReflectionTestUtils.setField(updateRequest, "isPinned", null);

            given(boardRepository.findByIdWithUserAndComments(anyLong()))
                    .willReturn(Optional.of(deletedBoard));

            // when & then
            BoardNotFoundException exception = assertThrows(BoardNotFoundException.class, () ->
                    boardService.updateBoard(1L, updateRequest, user)
            );

            verify(boardRepository).findByIdWithUserAndComments(1L);
            verify(boardRepository, never()).save(any(Board.class));
        }

        @Test
        void 공지사항_수정_권한_검증(){
            // given
            Board noticeBoard = new Board(
                    "공지사항",
                    "공지내용",
                    false,
                    true,
                    BoardType.NOTICE,
                    admin
            );
            ReflectionTestUtils.setField(noticeBoard, "id", 1L);
            ReflectionTestUtils.setField(noticeBoard, "status", BoardStatus.ACTIVE);

            BoardUpdateRequestDto updateRequest = new BoardUpdateRequestDto();
            ReflectionTestUtils.setField(updateRequest, "title", "수정된 공지");

            given(boardRepository.findByIdWithUserAndComments(anyLong()))
                    .willReturn(Optional.of(noticeBoard));

            // when & then
            // 일반 사용자의 공지사항 수정 시도
            assertThrows(BoardAuthorityException.class, () ->
                    boardService.updateBoard(1L, updateRequest, user)
            );
            verify(boardRepository, never()).save(any(Board.class));
        }
    }

    @Nested
    @DisplayName("게시글 삭제 테스트")
    class deleteBoardTest{
        @Test
        void 작성자의_게시글_삭제_성공(){
            // given
            Board board = new Board(
                    "테스트 게시글",
                    "테스트 내용",
                    false,
                    false,
                    BoardType.INQUIRY,
                    user
            );
            ReflectionTestUtils.setField(board, "id", 1L);
            ReflectionTestUtils.setField(board, "status", BoardStatus.ACTIVE);
            ReflectionTestUtils.setField(board, "viewCount", 0);

            given(boardRepository.findByIdWithUserAndComments(anyLong()))
                    .willReturn(Optional.of(board));
            given(boardRepository.save(any(Board.class))).willReturn(board);

            // when
            String result = boardService.deleteBoard(1L, user);

            // then
            assertEquals("게시글 삭제가 완료되었습니다.", result);
            assertEquals(BoardStatus.INACTIVE, board.getStatus());
            verify(boardRepository).findByIdWithUserAndComments(1L);
            verify(boardRepository).save(board);
        }

        @Test
        void 관리자의_타인_게시글_삭제_성공(){
            // given
            Board board = new Board(
                    "테스트 게시글",
                    "테스트 내용",
                    false,
                    false,
                    BoardType.INQUIRY,
                    user  // 일반 사용자가 작성한 게시글
            );
            ReflectionTestUtils.setField(board, "id", 1L);
            ReflectionTestUtils.setField(board, "status", BoardStatus.ACTIVE);
            ReflectionTestUtils.setField(board, "viewCount", 0);

            given(boardRepository.findByIdWithUserAndComments(anyLong()))
                    .willReturn(Optional.of(board));
            given(boardRepository.save(any(Board.class))).willReturn(board);

            // when
            String result = boardService.deleteBoard(1L, admin);

            // then
            assertEquals("게시글 삭제가 완료되었습니다.", result);
            assertEquals(BoardStatus.INACTIVE, board.getStatus());
            verify(boardRepository).findByIdWithUserAndComments(1L);
            verify(boardRepository).save(board);
        }

        @Test
        void 작성자가_아닌_사용자의_게시글_삭제_실패(){
            // given
            Board board = new Board(
                    "테스트 게시글",
                    "테스트 내용",
                    false,
                    false,
                    BoardType.INQUIRY,
                    user
            );
            ReflectionTestUtils.setField(board, "id", 1L);
            ReflectionTestUtils.setField(board, "status", BoardStatus.ACTIVE);
            ReflectionTestUtils.setField(board, "viewCount", 0);

            User otherUser = new User();
            ReflectionTestUtils.setField(otherUser, "id", 3L);
            ReflectionTestUtils.setField(otherUser, "role", UserRole.ROLE_USER);
            ReflectionTestUtils.setField(otherUser, "userName", "otherUser");

            given(boardRepository.findByIdWithUserAndComments(anyLong()))
                    .willReturn(Optional.of(board));

            // when & then
            assertThrows(BoardAuthorityException.class, () ->
                    boardService.deleteBoard(1L, otherUser)
            );
            assertEquals(BoardStatus.ACTIVE, board.getStatus());  // 상태가 변경되지 않음을 확인
            verify(boardRepository, never()).save(any(Board.class));
        }

        @Test
        void 존재하지_않는_게시글_삭제_실패(){
            // given
            given(boardRepository.findByIdWithUserAndComments(anyLong()))
                    .willReturn(Optional.empty());

            // when & then
            assertThrows(BoardNotFoundException.class, () ->
                    boardService.deleteBoard(999L, user)
            );
            verify(boardRepository, never()).save(any(Board.class));
        }

        @Test
        void 이미_삭제된_게시글_삭제_실패(){
            // given
            Board deletedBoard = new Board(
                    "삭제된 게시글",
                    "삭제된 내용",
                    false,
                    false,
                    BoardType.INQUIRY,
                    user
            );
            ReflectionTestUtils.setField(deletedBoard, "id", 1L);
            ReflectionTestUtils.setField(deletedBoard, "status", BoardStatus.INACTIVE);
            ReflectionTestUtils.setField(deletedBoard, "viewCount", 0);

            given(boardRepository.findByIdWithUserAndComments(anyLong()))
                    .willReturn(Optional.of(deletedBoard));

            // when & then
            assertThrows(BoardNotFoundException.class, () ->
                    boardService.deleteBoard(1L, user)
            );
            verify(boardRepository, never()).save(any(Board.class));
        }

        @Test
        void 비밀글_삭제_권한_검증(){
            // given
            Board secretBoard = new Board(
                    "비밀 게시글",
                    "비밀 내용",
                    true,
                    false,
                    BoardType.INQUIRY,
                    user
            );
            ReflectionTestUtils.setField(secretBoard, "id", 1L);
            ReflectionTestUtils.setField(secretBoard, "status", BoardStatus.ACTIVE);
            ReflectionTestUtils.setField(secretBoard, "viewCount", 0);

            User otherUser = new User();
            ReflectionTestUtils.setField(otherUser, "id", 3L);
            ReflectionTestUtils.setField(otherUser, "role", UserRole.ROLE_USER);
            ReflectionTestUtils.setField(otherUser, "userName", "otherUser");

            given(boardRepository.findByIdWithUserAndComments(anyLong()))
                    .willReturn(Optional.of(secretBoard));

            // when & then
            // 다른 사용자의 비밀글 삭제 시도
            assertThrows(BoardAuthorityException.class, () ->
                    boardService.deleteBoard(1L, otherUser)
            );
            assertEquals(BoardStatus.ACTIVE, secretBoard.getStatus());
            verify(boardRepository, never()).save(any(Board.class));
        }

        @Test
        void 공지사항_삭제_권한_검증(){
            // given
            Board noticeBoard = new Board(
                    "공지사항",
                    "공지사항 내용",
                    false,
                    true,
                    BoardType.NOTICE,
                    admin
            );
            ReflectionTestUtils.setField(noticeBoard, "id", 1L);
            ReflectionTestUtils.setField(noticeBoard, "status", BoardStatus.ACTIVE);
            ReflectionTestUtils.setField(noticeBoard, "viewCount", 0);

            given(boardRepository.findByIdWithUserAndComments(anyLong()))
                    .willReturn(Optional.of(noticeBoard));

            // when & then
            // 일반 사용자의 공지사항 삭제 시도
            assertThrows(BoardAuthorityException.class, () ->
                    boardService.deleteBoard(1L, user)
            );
            assertEquals(BoardStatus.ACTIVE, noticeBoard.getStatus());
            verify(boardRepository, never()).save(any(Board.class));
        }
    }
}