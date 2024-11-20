package com.example.library_management.domain.boardComment.service;

import com.example.library_management.domain.board.entity.Board;
import com.example.library_management.domain.board.enums.BoardStatus;
import com.example.library_management.domain.board.enums.BoardType;
import com.example.library_management.domain.board.exception.BoardNotFoundException;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.example.library_management.domain.common.exception.GlobalExceptionConst.MISMATCHED_COMMENT_BOARD;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BoardCommentServiceTest {
    @Mock
    private BoardCommentRepository boardCommentRepository;

    @Mock
    private BoardService boardService;

    @InjectMocks
    private BoardCommentService boardCommentService;

    private User user;
    private Board board;
    private BoardComment comment;
    private BoardCommentRequestDto requestDto;

    @BeforeEach
    void setUp() {
        // 일반 사용자 설정
        user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(user, "userName", "testUser");
        ReflectionTestUtils.setField(user, "role", UserRole.ROLE_USER);

        // 게시글 설정
        board = new Board(
                "테스트 게시글",
                "테스트 내용",
                false,
                false,
                BoardType.INQUIRY,
                user
        );
        ReflectionTestUtils.setField(board, "id", 1L);
        ReflectionTestUtils.setField(board, "status", BoardStatus.ACTIVE);
    }

    @Nested
    @DisplayName("댓글 작성 테스트")
    class createCommentTest{
        @Test
        void 댓글_작성_성공(){
            // given
            requestDto = new BoardCommentRequestDto();
            ReflectionTestUtils.setField(requestDto, "content", "테스트 댓글입니다.");

            comment = BoardComment.builder()
                    .content(requestDto.getContent())
                    .board(board)
                    .user(user)
                    .build();
            ReflectionTestUtils.setField(comment, "id", 1L);

            given(boardService.findBoardById(any(Long.class))).willReturn(board);
            given(boardCommentRepository.save(any(BoardComment.class))).willReturn(comment);

            // when
            BoardCommentResponseDto responseDto = boardCommentService.createComment(1L, requestDto, user);

            // then
            assertNotNull(responseDto);
            assertEquals(1L, responseDto.getId());
            assertEquals("테스트 댓글입니다.", responseDto.getContent());
            assertEquals("testUser", responseDto.getUsername());
            verify(boardCommentRepository).save(any(BoardComment.class));
            verify(boardService).findBoardById(1L);
        }

        @Test
        void 존재하지_않는_게시글에_댓글_작성_실패(){
            // given
            requestDto = new BoardCommentRequestDto();
            ReflectionTestUtils.setField(requestDto, "content", "테스트 댓글입니다.");

            given(boardService.findBoardById(any(Long.class)))
                    .willThrow(new BoardNotFoundException());

            // when & then
            assertThrows(BoardNotFoundException.class, () ->
                    boardCommentService.createComment(999L, requestDto, user)
            );
        }

        @Test
        void 삭제된_게시글에_댓글_작성_실패(){
            // given
            requestDto = new BoardCommentRequestDto();
            ReflectionTestUtils.setField(requestDto, "content", "테스트 댓글입니다.");

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

            given(boardService.findBoardById(any(Long.class)))
                    .willThrow(new BoardNotFoundException());

            // when & then
            assertThrows(BoardNotFoundException.class, () ->
                    boardCommentService.createComment(1L, requestDto, user)
            );
        }

        @Test
        void 관리자_댓글_작성_성공(){
            // given
            User admin = new User();
            ReflectionTestUtils.setField(admin, "id", 2L);
            ReflectionTestUtils.setField(admin, "userName", "admin");
            ReflectionTestUtils.setField(admin, "role", UserRole.ROLE_ADMIN);

            requestDto = new BoardCommentRequestDto();
            ReflectionTestUtils.setField(requestDto, "content", "관리자 댓글입니다.");

            comment = BoardComment.builder()
                    .content(requestDto.getContent())
                    .board(board)
                    .user(admin)
                    .build();
            ReflectionTestUtils.setField(comment, "id", 2L);

            given(boardService.findBoardById(any(Long.class))).willReturn(board);
            given(boardCommentRepository.save(any(BoardComment.class))).willReturn(comment);

            // when
            BoardCommentResponseDto responseDto = boardCommentService.createComment(1L, requestDto, admin);

            // then
            assertNotNull(responseDto);
            assertEquals("관리자 댓글입니다.", responseDto.getContent());
            assertEquals("admin", responseDto.getUsername());
            verify(boardCommentRepository).save(any(BoardComment.class));
        }
    }

    @Nested
    @DisplayName("댓글 수정 테스트")
    class updateCommentTest{
        @Test
        void 작성자의_댓글_수정_성공(){
            // given
            BoardComment comment = BoardComment.builder()
                    .content("원본 댓글 내용")
                    .board(board)
                    .user(user)
                    .build();
            ReflectionTestUtils.setField(comment, "id", 1L);

            BoardCommentRequestDto updateRequestDto = new BoardCommentRequestDto();
            ReflectionTestUtils.setField(updateRequestDto, "content", "수정된 댓글 내용");

            given(boardService.findBoardById(any(Long.class))).willReturn(board);
            given(boardCommentRepository.findByIdWithUserAndBoard(any(Long.class)))
                    .willReturn(Optional.of(comment));
            given(boardCommentRepository.save(any(BoardComment.class))).willReturn(comment);

            // when
            BoardCommentResponseDto responseDto = boardCommentService.updateComment(1L, 1L, updateRequestDto, user);

            // then
            assertNotNull(responseDto);
            assertEquals("수정된 댓글 내용", responseDto.getContent());
            assertEquals("testUser", responseDto.getUsername());
            verify(boardCommentRepository).save(any(BoardComment.class));
        }

        @Test
        void 관리자의_댓글_수정_성공(){
            // given
            BoardComment comment = BoardComment.builder()
                    .content("원본 댓글 내용")
                    .board(board)
                    .user(user)
                    .build();
            ReflectionTestUtils.setField(comment, "id", 1L);

            User admin = new User();
            ReflectionTestUtils.setField(admin, "id", 2L);
            ReflectionTestUtils.setField(admin, "userName", "admin");
            ReflectionTestUtils.setField(admin, "role", UserRole.ROLE_ADMIN);

            BoardCommentRequestDto updateRequestDto = new BoardCommentRequestDto();
            ReflectionTestUtils.setField(updateRequestDto, "content", "관리자가 수정한 내용");

            given(boardService.findBoardById(any(Long.class))).willReturn(board);
            given(boardCommentRepository.findByIdWithUserAndBoard(any(Long.class)))
                    .willReturn(Optional.of(comment));
            given(boardCommentRepository.save(any(BoardComment.class))).willReturn(comment);

            // when
            BoardCommentResponseDto responseDto = boardCommentService.updateComment(1L, 1L, updateRequestDto, admin);

            // then
            assertNotNull(responseDto);
            assertEquals("관리자가 수정한 내용", responseDto.getContent());
            assertEquals("testUser", responseDto.getUsername()); // 원래 작성자 이름 유지
            verify(boardCommentRepository).save(any(BoardComment.class));
        }

        @Test
        void 작성자가_아닌_사용자의_댓글_수정_실패(){
            // given
            BoardComment comment = BoardComment.builder()
                    .content("원본 댓글 내용")
                    .board(board)
                    .user(user)
                    .build();
            ReflectionTestUtils.setField(comment, "id", 1L);

            User otherUser = new User();
            ReflectionTestUtils.setField(otherUser, "id", 3L);
            ReflectionTestUtils.setField(otherUser, "userName", "otherUser");
            ReflectionTestUtils.setField(otherUser, "role", UserRole.ROLE_USER);

            BoardCommentRequestDto updateRequestDto = new BoardCommentRequestDto();
            ReflectionTestUtils.setField(updateRequestDto, "content", "다른 사용자가 수정한 내용");

            given(boardService.findBoardById(any(Long.class))).willReturn(board);
            given(boardCommentRepository.findByIdWithUserAndBoard(any(Long.class)))
                    .willReturn(Optional.of(comment));

            // when & then
            assertThrows(UnauthorizedCommentAccessException.class, () ->
                    boardCommentService.updateComment(1L, 1L, updateRequestDto, otherUser)
            );
            verify(boardCommentRepository, never()).save(any(BoardComment.class));
        }

        @Test
        void 게시글과_댓글_ID_불일치_시_수정_실패() {
            // given
            Board board1 = new Board(
                    "게시글 1",
                    "내용 1",
                    false,
                    false,
                    BoardType.INQUIRY,
                    user
            );
            ReflectionTestUtils.setField(board1, "id", 1L);
            ReflectionTestUtils.setField(board1, "status", BoardStatus.ACTIVE);

            BoardComment comment = BoardComment.builder()
                    .content("원본 댓글")
                    .board(board1)  // board의 id는 1L
                    .user(user)
                    .build();
            ReflectionTestUtils.setField(comment, "id", 1L);

            BoardCommentRequestDto updateRequestDto = new BoardCommentRequestDto();
            ReflectionTestUtils.setField(updateRequestDto, "content", "수정할 내용");

            given(boardService.findBoardById(anyLong())).willReturn(board1);
            given(boardCommentRepository.findByIdWithUserAndBoard(anyLong()))
                    .willReturn(Optional.of(comment));

            // when & then
            // 다른 게시글 ID(2L)로 수정 시도
            CommentMismatchException exception = assertThrows(CommentMismatchException.class, () ->
                    boardCommentService.updateComment(2L, 1L, updateRequestDto, user)
            );

            // 예외 상태 코드와 메시지 검증
            assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
            assertTrue(exception.getMessage().contains("해당 게시글의 댓글이 아닙니다"));

            // Repository save 메서드가 호출되지 않았는지 검증
            verify(boardService).findBoardById(2L);
            verify(boardCommentRepository).findByIdWithUserAndBoard(1L);
            verify(boardCommentRepository, never()).save(any(BoardComment.class));
        }

        @Test
        void 존재하지_않는_댓글_수정_실패() {
            // given
            BoardCommentRequestDto updateRequestDto = new BoardCommentRequestDto();
            ReflectionTestUtils.setField(updateRequestDto, "content", "수정된 내용");

            given(boardService.findBoardById(any(Long.class))).willReturn(board);
            given(boardCommentRepository.findByIdWithUserAndBoard(any(Long.class)))
                    .willReturn(Optional.empty());

            // when & then
            assertThrows(CommentNouFoundException.class, () ->
                    boardCommentService.updateComment(1L, 999L, updateRequestDto, user)
            );
            verify(boardCommentRepository, never()).save(any(BoardComment.class));
        }

        @Test
        void 존재하지_않는_게시글의_댓글_수정_실패() {
            // given
            BoardCommentRequestDto updateRequestDto = new BoardCommentRequestDto();
            ReflectionTestUtils.setField(updateRequestDto, "content", "수정된 내용");

            given(boardService.findBoardById(any(Long.class)))
                    .willThrow(new BoardNotFoundException());

            // when & then
            assertThrows(BoardNotFoundException.class, () ->
                    boardCommentService.updateComment(999L, 1L, updateRequestDto, user)
            );
            verify(boardCommentRepository, never()).findByIdWithUserAndBoard(any());
            verify(boardCommentRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("게시글 모든 댓글 조회 테스트")
    class getCommentListTest{
        @Test
        void 게시글의_모든_댓글_조회_성공(){
            // given
            // 첫 번째 댓글
            BoardComment comment1 = BoardComment.builder()
                    .content("첫 번째 댓글")
                    .board(board)
                    .user(user)
                    .build();
            ReflectionTestUtils.setField(comment1, "id", 1L);

            // 두 번째 댓글 (다른 사용자)
            User otherUser = new User();
            ReflectionTestUtils.setField(otherUser, "id", 3L);
            ReflectionTestUtils.setField(otherUser, "userName", "otherUser");
            ReflectionTestUtils.setField(otherUser, "role", UserRole.ROLE_USER);

            BoardComment comment2 = BoardComment.builder()
                    .content("두 번째 댓글")
                    .board(board)
                    .user(otherUser)
                    .build();
            ReflectionTestUtils.setField(comment2, "id", 2L);

            // 관리자 댓글
            User admin = new User();
            ReflectionTestUtils.setField(admin, "id", 2L);
            ReflectionTestUtils.setField(admin, "userName", "admin");
            ReflectionTestUtils.setField(admin, "role", UserRole.ROLE_ADMIN);

            BoardComment comment3 = BoardComment.builder()
                    .content("관리자 댓글")
                    .board(board)
                    .user(admin)
                    .build();
            ReflectionTestUtils.setField(comment3, "id", 3L);

            List<BoardComment> commentList = Arrays.asList(comment1, comment2, comment3);

            given(boardCommentRepository.findByBoardId(anyLong()))
                    .willReturn(commentList);

            // when
            List<BoardCommentResponseDto> responseDtoList = boardCommentService.getCommentList(1L);

            // then
            assertNotNull(responseDtoList);
            assertEquals(3, responseDtoList.size());

            // 첫 번째 댓글 검증
            assertEquals(1L, responseDtoList.get(0).getId());
            assertEquals("첫 번째 댓글", responseDtoList.get(0).getContent());
            assertEquals("testUser", responseDtoList.get(0).getUsername());

            // 두 번째 댓글 검증
            assertEquals(2L, responseDtoList.get(1).getId());
            assertEquals("두 번째 댓글", responseDtoList.get(1).getContent());
            assertEquals("otherUser", responseDtoList.get(1).getUsername());

            // 세 번째 댓글 검증
            assertEquals(3L, responseDtoList.get(2).getId());
            assertEquals("관리자 댓글", responseDtoList.get(2).getContent());
            assertEquals("admin", responseDtoList.get(2).getUsername());

            verify(boardCommentRepository).findByBoardId(1L);
        }

        @Test
        void 댓글이_없는_게시글_조회_성공(){
            // given
            given(boardCommentRepository.findByBoardId(anyLong()))
                    .willReturn(Collections.emptyList());

            // when
            List<BoardCommentResponseDto> responseDtoList = boardCommentService.getCommentList(1L);

            // then
            assertNotNull(responseDtoList);
            assertTrue(responseDtoList.isEmpty());
            verify(boardCommentRepository).findByBoardId(1L);
        }

        @Test
        void 댓글_목록_조회_날짜순_정렬_확인(){
            // given
            // 3일 전 댓글
            BoardComment oldComment = BoardComment.builder()
                    .content("3일 전 댓글")
                    .board(board)
                    .user(user)
                    .build();
            ReflectionTestUtils.setField(oldComment, "id", 1L);
            ReflectionTestUtils.setField(oldComment, "createdAt", LocalDateTime.now().minusDays(3));

            // 1일 전 댓글
            BoardComment recentComment = BoardComment.builder()
                    .content("1일 전 댓글")
                    .board(board)
                    .user(user)
                    .build();
            ReflectionTestUtils.setField(recentComment, "id", 2L);
            ReflectionTestUtils.setField(recentComment, "createdAt", LocalDateTime.now().minusDays(1));

            // 현재 댓글
            BoardComment newComment = BoardComment.builder()
                    .content("최신 댓글")
                    .board(board)
                    .user(user)
                    .build();
            ReflectionTestUtils.setField(newComment, "id", 3L);
            ReflectionTestUtils.setField(newComment, "createdAt", LocalDateTime.now());

            List<BoardComment> commentList = Arrays.asList(oldComment, recentComment, newComment);

            given(boardCommentRepository.findByBoardId(anyLong()))
                    .willReturn(commentList);

            // when
            List<BoardCommentResponseDto> responseDtoList = boardCommentService.getCommentList(1L);

            // then
            assertNotNull(responseDtoList);
            assertEquals(3, responseDtoList.size());

            // 날짜순 정렬 확인
            assertTrue(responseDtoList.get(0).getCreatedAt()
                    .isBefore(responseDtoList.get(1).getCreatedAt()));
            assertTrue(responseDtoList.get(1).getCreatedAt()
                    .isBefore(responseDtoList.get(2).getCreatedAt()));

            verify(boardCommentRepository).findByBoardId(1L);
        }

        @Test
        void 삭제된_게시글의_댓글_목록_조회() {
            // given
            // 삭제된 게시글 설정
            Board deletedBoard = new Board(
                    "삭제된 게시글",
                    "삭제된 내용",
                    false,
                    false,
                    BoardType.INQUIRY,
                    user
            );
            ReflectionTestUtils.setField(deletedBoard, "id", 2L);
            ReflectionTestUtils.setField(deletedBoard, "status", BoardStatus.INACTIVE);

            BoardComment comment = BoardComment.builder()
                    .content("삭제된 게시글의 댓글")
                    .board(deletedBoard)
                    .user(user)
                    .build();
            ReflectionTestUtils.setField(comment, "id", 1L);

            List<BoardComment> commentList = Collections.singletonList(comment);

            given(boardCommentRepository.findByBoardId(anyLong()))
                    .willReturn(commentList);

            // when
            List<BoardCommentResponseDto> responseDtoList = boardCommentService.getCommentList(2L);

            // then
            assertNotNull(responseDtoList);
            assertEquals(1, responseDtoList.size());
            assertEquals("삭제된 게시글의 댓글", responseDtoList.get(0).getContent());
            verify(boardCommentRepository).findByBoardId(2L);
        }

        @Test
        void 댓글_수정_이력_확인() {
            // given
            BoardComment comment = BoardComment.builder()
                    .content("수정된 댓글")
                    .board(board)
                    .user(user)
                    .build();
            ReflectionTestUtils.setField(comment, "id", 1L);

            // 생성 시간과 수정 시간을 다르게 설정
            LocalDateTime createdAt = LocalDateTime.now().minusHours(2);
            LocalDateTime modifiedAt = LocalDateTime.now().minusHours(1);
            ReflectionTestUtils.setField(comment, "createdAt", createdAt);
            ReflectionTestUtils.setField(comment, "modifiedAt", modifiedAt);

            List<BoardComment> commentList = Collections.singletonList(comment);

            given(boardCommentRepository.findByBoardId(anyLong()))
                    .willReturn(commentList);

            // when
            List<BoardCommentResponseDto> responseDtoList = boardCommentService.getCommentList(1L);

            // then
            assertNotNull(responseDtoList);
            assertEquals(1, responseDtoList.size());
            assertTrue(responseDtoList.get(0).getModifiedAt().isAfter(responseDtoList.get(0).getCreatedAt()));
            verify(boardCommentRepository).findByBoardId(1L);
        }
    }

    @Nested
    @DisplayName("댓글 삭제 테스트")
    class deleteCommentTest{
        @Test
        @DisplayName("작성자의 댓글 삭제 성공")
        void deleteComment_ByAuthorSuccess() {
            // given
            BoardComment comment = BoardComment.builder()
                    .content("삭제할 댓글")
                    .board(board)
                    .user(user)
                    .build();
            ReflectionTestUtils.setField(comment, "id", 1L);

            given(boardCommentRepository.findByIdWithUserAndBoard(anyLong()))
                    .willReturn(Optional.of(comment));

            // when
            String result = boardCommentService.deleteComment(1L, 1L, user);

            // then
            assertEquals("댓글 삭제가 완료되었습니다", result);
            verify(boardCommentRepository).findByIdWithUserAndBoard(1L);
            verify(boardCommentRepository).delete(comment);
        }

        @Test
        @DisplayName("관리자의 타인 댓글 삭제 성공")
        void deleteComment_ByAdminSuccess() {
            // given
            BoardComment comment = BoardComment.builder()
                    .content("삭제할 댓글")
                    .board(board)
                    .user(user)  // 일반 사용자가 작성한 댓글
                    .build();
            ReflectionTestUtils.setField(comment, "id", 1L);

            User admin = new User();
            ReflectionTestUtils.setField(admin, "id", 2L);
            ReflectionTestUtils.setField(admin, "userName", "admin");
            ReflectionTestUtils.setField(admin, "role", UserRole.ROLE_ADMIN);

            given(boardCommentRepository.findByIdWithUserAndBoard(anyLong()))
                    .willReturn(Optional.of(comment));

            // when
            String result = boardCommentService.deleteComment(1L, 1L, admin);

            // then
            assertEquals("댓글 삭제가 완료되었습니다", result);
            verify(boardCommentRepository).findByIdWithUserAndBoard(1L);
            verify(boardCommentRepository).delete(comment);
        }

        @Test
        @DisplayName("작성자가 아닌 사용자의 댓글 삭제 실패")
        void deleteComment_ByNonAuthorFail() {
            // given
            BoardComment comment = BoardComment.builder()
                    .content("삭제할 댓글")
                    .board(board)
                    .user(user)
                    .build();
            ReflectionTestUtils.setField(comment, "id", 1L);

            User otherUser = new User();
            ReflectionTestUtils.setField(otherUser, "id", 3L);
            ReflectionTestUtils.setField(otherUser, "userName", "otherUser");
            ReflectionTestUtils.setField(otherUser, "role", UserRole.ROLE_USER);

            given(boardCommentRepository.findByIdWithUserAndBoard(anyLong()))
                    .willReturn(Optional.of(comment));

            // when & then
            assertThrows(UnauthorizedCommentAccessException.class, () ->
                    boardCommentService.deleteComment(1L, 1L, otherUser)
            );
            verify(boardCommentRepository, never()).delete(any(BoardComment.class));
        }

        @Test
        @DisplayName("존재하지 않는 댓글 삭제 실패")
        void deleteNonExistentComment_Fail() {
            // given
            given(boardCommentRepository.findByIdWithUserAndBoard(anyLong()))
                    .willReturn(Optional.empty());

            // when & then
            assertThrows(CommentNouFoundException.class, () ->
                    boardCommentService.deleteComment(1L, 999L, user)
            );
            verify(boardCommentRepository, never()).delete(any(BoardComment.class));
        }

        @Test
        @DisplayName("삭제된 게시글의 댓글 삭제")
        void deleteCommentFromDeletedBoard_Success() {
            // given
            Board deletedBoard = new Board(
                    "삭제된 게시글",
                    "삭제된 내용",
                    false,
                    false,
                    BoardType.INQUIRY,
                    user
            );
            ReflectionTestUtils.setField(deletedBoard, "id", 2L);
            ReflectionTestUtils.setField(deletedBoard, "status", BoardStatus.INACTIVE);

            BoardComment comment = BoardComment.builder()
                    .content("삭제할 댓글")
                    .board(deletedBoard)
                    .user(user)
                    .build();
            ReflectionTestUtils.setField(comment, "id", 1L);

            given(boardCommentRepository.findByIdWithUserAndBoard(anyLong()))
                    .willReturn(Optional.of(comment));

            // when
            String result = boardCommentService.deleteComment(2L, 1L, user);

            // then
            assertEquals("댓글 삭제가 완료되었습니다", result);
            verify(boardCommentRepository).findByIdWithUserAndBoard(1L);
            verify(boardCommentRepository).delete(comment);
        }

        @Test
        @DisplayName("다중 댓글 삭제 시 개별 댓글 권한 검증")
        void deleteMultipleComments_AuthorityCheck() {
            // given
            User otherUser = new User();
            ReflectionTestUtils.setField(otherUser, "id", 3L);
            ReflectionTestUtils.setField(otherUser, "userName", "otherUser");
            ReflectionTestUtils.setField(otherUser, "role", UserRole.ROLE_USER);

            BoardComment userComment = BoardComment.builder()
                    .content("사용자의 댓글")
                    .board(board)
                    .user(user)
                    .build();
            ReflectionTestUtils.setField(userComment, "id", 1L);

            BoardComment otherComment = BoardComment.builder()
                    .content("다른 사용자의 댓글")
                    .board(board)
                    .user(otherUser)
                    .build();
            ReflectionTestUtils.setField(otherComment, "id", 2L);

            given(boardCommentRepository.findByIdWithUserAndBoard(1L))
                    .willReturn(Optional.of(userComment));
            given(boardCommentRepository.findByIdWithUserAndBoard(2L))
                    .willReturn(Optional.of(otherComment));

            // when & then
            // 자신의 댓글 삭제 성공
            String result1 = boardCommentService.deleteComment(1L, 1L, user);
            assertEquals("댓글 삭제가 완료되었습니다", result1);
            verify(boardCommentRepository).delete(userComment);

            // 타인의 댓글 삭제 시도 실패
            assertThrows(UnauthorizedCommentAccessException.class, () ->
                    boardCommentService.deleteComment(1L, 2L, user)
            );
            verify(boardCommentRepository, never()).delete(otherComment);
        }

        @Test
        @DisplayName("게시글과 댓글 ID 불일치 시 삭제 실패")
        void deleteCommentWithWrongBoardId_Fail() {
            // given
            // board id가 1L인 댓글
            BoardComment comment = BoardComment.builder()
                    .content("삭제할 댓글")
                    .board(board)  // board의 id는 1L
                    .user(user)
                    .build();
            ReflectionTestUtils.setField(comment, "id", 1L);

            given(boardCommentRepository.findByIdWithUserAndBoard(anyLong()))
                    .willReturn(Optional.of(comment));

            // when & then
            // 다른 게시글 ID(2L)로 삭제 시도
            CommentMismatchException exception = assertThrows(CommentMismatchException.class, () ->
                    boardCommentService.deleteComment(2L, 1L, user)
            );

            // 예외 상태 코드와 메시지 검증
            assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
            assertTrue(exception.getMessage().contains("해당 게시글의 댓글이 아닙니다"));
            verify(boardCommentRepository, never()).delete(any(BoardComment.class));
        }
    }
}