package com.example.library_management.domain.board.controller;

import com.example.library_management.domain.board.dto.request.BoardCreateRequestDto;
import com.example.library_management.domain.board.dto.request.BoardSearchCondition;
import com.example.library_management.domain.board.dto.request.BoardUpdateRequestDto;
import com.example.library_management.domain.board.dto.response.BoardResponseDto;
import com.example.library_management.domain.board.dto.response.BoardSearchResult;
import com.example.library_management.domain.board.entity.Board;
import com.example.library_management.domain.board.enums.BoardSearchType;
import com.example.library_management.domain.board.enums.BoardStatus;
import com.example.library_management.domain.board.enums.BoardType;
import com.example.library_management.domain.board.exception.BoardAuthorityException;
import com.example.library_management.domain.board.exception.BoardNotFoundException;
import com.example.library_management.domain.board.service.BoardService;
import com.example.library_management.domain.user.entity.User;
import com.example.library_management.domain.user.enums.UserRole;
import com.example.library_management.global.security.UserDetailsImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BoardController.class)
@MockBean(JpaMetamodelMappingContext.class)
class BoardControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BoardService boardService;

    @Nested
    @DisplayName("게시글 작성 테스트")
    class createBoardTest{
        @Test
        @WithMockUser
        void 게시글_작성_성공() throws Exception{
            // given
            BoardCreateRequestDto requestDto = new BoardCreateRequestDto();
            ReflectionTestUtils.setField(requestDto, "title", "테스트 제목");
            ReflectionTestUtils.setField(requestDto, "content", "테스트 내용");
            ReflectionTestUtils.setField(requestDto, "boardType", BoardType.NOTICE);
            ReflectionTestUtils.setField(requestDto, "isSecret", false);
            ReflectionTestUtils.setField(requestDto, "isPinned", false);

            Board board = new Board(
                    requestDto.getTitle(),
                    requestDto.getContent(),
                    requestDto.isSecret(),
                    requestDto.isPinned(),
                    requestDto.getBoardType(),
                    mock(User.class)
            );
            BoardResponseDto responseDto = new BoardResponseDto(board);

            User mockUser = mock(User.class);
            when(mockUser.getRole()).thenReturn(UserRole.ROLE_ADMIN);
            UserDetailsImpl userDetails = new UserDetailsImpl(mockUser);

            when(boardService.createBoard(any(BoardCreateRequestDto.class), any(User.class)))
                    .thenReturn(responseDto);

            // when & then
            mockMvc.perform(post("/library/board")
                            .with(csrf())
                            .with(user(userDetails))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.title").value("테스트 제목"))
                    .andExpect(jsonPath("$.data.content").value("테스트 내용"))
                    .andExpect(jsonPath("$.data.boardType").value("NOTICE"))
                    .andExpect(jsonPath("$.data.secret").value(false))
                    .andExpect(jsonPath("$.data.pinned").value(false))
                    .andDo(print());
        }

        @Test
        @WithMockUser
        void 제목_누락으로_게시글_작성_실패() throws Exception{
            // given
            BoardCreateRequestDto requestDto = new BoardCreateRequestDto();
            ReflectionTestUtils.setField(requestDto, "content", "테스트 내용");
            ReflectionTestUtils.setField(requestDto, "boardType", BoardType.NOTICE);

            User mockUser = mock(User.class);
            when(mockUser.getRole()).thenReturn(UserRole.ROLE_ADMIN);
            UserDetailsImpl userDetails = new UserDetailsImpl(mockUser);

            // when & then
            mockMvc.perform(post("/library/board")
                            .with(csrf())
                            .with(user(userDetails))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @Test
        @WithMockUser
        void 권한_불일치로_게시글_작성_실패() throws Exception{
            // given
            BoardCreateRequestDto requestDto = new BoardCreateRequestDto();
            ReflectionTestUtils.setField(requestDto, "title", "테스트 제목");
            ReflectionTestUtils.setField(requestDto, "content", "테스트 내용");
            ReflectionTestUtils.setField(requestDto, "boardType", BoardType.NOTICE);

            User mockUser = mock(User.class);
            when(mockUser.getRole()).thenReturn(UserRole.ROLE_USER);
            UserDetailsImpl userDetails = new UserDetailsImpl(mockUser);

            when(boardService.createBoard(any(BoardCreateRequestDto.class), any(User.class)))
                    .thenThrow(new BoardAuthorityException());

            // when & then
            mockMvc.perform(post("/library/board")
                            .with(csrf())
                            .with(user(userDetails))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isForbidden())
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("게시글 상세 조회 테스트")
    class getBoardTest{
        @Test
        @WithMockUser
        void 게시글_상세_조회_성공() throws Exception{
            // given
            Long boardId = 1L;

            User mockUser = mock(User.class);
            when(mockUser.getRole()).thenReturn(UserRole.ROLE_USER);
            when(mockUser.getUserName()).thenReturn("testUser");

            Board board = new Board(
                    "테스트 제목",
                    "테스트 내용",
                    false,
                    false,
                    BoardType.NOTICE,
                    mockUser
            );
            ReflectionTestUtils.setField(board, "id", boardId);
            ReflectionTestUtils.setField(board, "viewCount", 0);
            ReflectionTestUtils.setField(board, "status", BoardStatus.ACTIVE);

            BoardResponseDto responseDto = new BoardResponseDto(board);

            UserDetailsImpl userDetails = new UserDetailsImpl(mockUser);

            when(boardService.getBoard(eq(boardId), any(User.class)))
                    .thenReturn(responseDto);

            // when & then
            mockMvc.perform(get("/library/board/{boardId}", boardId)
                            .with(user(userDetails)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.id").value(boardId))
                    .andExpect(jsonPath("$.data.title").value("테스트 제목"))
                    .andExpect(jsonPath("$.data.content").value("테스트 내용"))
                    .andExpect(jsonPath("$.data.viewCount").value(0))
                    .andExpect(jsonPath("$.data.secret").value(false))
                    .andExpect(jsonPath("$.data.pinned").value(false))
                    .andExpect(jsonPath("$.data.boardType").value("NOTICE"))
                    .andExpect(jsonPath("$.data.status").value("ACTIVE"))
                    .andExpect(jsonPath("$.data.username").value("testUser"))
                    .andDo(print());
        }

        @Test
        @WithMockUser
        void 존재하지_않는_게시글_상세_조회_실패() throws Exception{
            // given
            Long boardId = 999L;

            User mockUser = mock(User.class);
            when(mockUser.getRole()).thenReturn(UserRole.ROLE_USER);
            UserDetailsImpl userDetails = new UserDetailsImpl(mockUser);

            when(boardService.getBoard(eq(boardId), any(User.class)))
                    .thenThrow(new BoardNotFoundException());

            // when & then
            mockMvc.perform(get("/library/board/{boardId}", boardId)
                            .with(user(userDetails)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value("ERROR"))
                    .andDo(print());
        }

        @Test
        @WithMockUser
        void 비밀글_접근_권한_없어서_게시글_상세_조회_실패() throws Exception{
            // given
            Long boardId = 1L;

            User mockUser = mock(User.class);
            when(mockUser.getRole()).thenReturn(UserRole.ROLE_USER);
            UserDetailsImpl userDetails = new UserDetailsImpl(mockUser);

            when(boardService.getBoard(eq(boardId), any(User.class)))
                    .thenThrow(new BoardAuthorityException());

            // when & then
            mockMvc.perform(get("/library/board/{boardId}", boardId)
                            .with(user(userDetails)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.status").value("ERROR"))
                    .andDo(print());
        }

        @Test
        @WithMockUser
        void 삭제된_게시글이라서_게시글_상세_조회_실패() throws Exception{
            // given
            Long boardId = 1L;

            User mockUser = mock(User.class);
            when(mockUser.getRole()).thenReturn(UserRole.ROLE_USER);
            UserDetailsImpl userDetails = new UserDetailsImpl(mockUser);

            when(boardService.getBoard(eq(boardId), any(User.class)))
                    .thenThrow(new BoardNotFoundException());

            // when & then
            mockMvc.perform(get("/library/board/{boardId}", boardId)
                            .with(user(userDetails)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value("ERROR"))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("게시글 검색 테스트")
    class searchBoardListTest{
        @Test
        @WithMockUser
        void 게시글_검색_성공() throws Exception{
            // given
            BoardSearchCondition condition = new BoardSearchCondition();
            ReflectionTestUtils.setField(condition, "keyword", "테스트");
            ReflectionTestUtils.setField(condition, "boardType", BoardType.NOTICE);
            ReflectionTestUtils.setField(condition, "searchType", BoardSearchType.TITLE);
            ReflectionTestUtils.setField(condition, "includeSecret", false);

            List<BoardSearchResult> content = List.of(
                    new BoardSearchResult(1L, "테스트 제목1", "testUser1",
                            0, false, false, LocalDateTime.now(), 0),
                    new BoardSearchResult(2L, "테스트 제목2", "testUser2",
                            0, false, false, LocalDateTime.now(), 0)
            );

            Page<BoardSearchResult> pageResult = new PageImpl<>(content);

            User mockUser = mock(User.class);
            when(mockUser.getRole()).thenReturn(UserRole.ROLE_USER);
            UserDetailsImpl userDetails = new UserDetailsImpl(mockUser);

            when(boardService.searchBoardList(any(BoardSearchCondition.class), any(Pageable.class), any(User.class)))
                    .thenReturn(pageResult);

            // when & then
            mockMvc.perform(get("/library/board/search")
                            .with(user(userDetails))
                            .param("keyword", "테스트")
                            .param("boardType", BoardType.NOTICE.name())
                            .param("searchType", BoardSearchType.TITLE.name())
                            .param("includeSecret", "false"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content.length()").value(2))
                    .andExpect(jsonPath("$.data.content[0].id").value(1))
                    .andExpect(jsonPath("$.data.content[0].title").value("테스트 제목1"))
                    .andExpect(jsonPath("$.data.content[0].writerName").value("testUser1"))
                    .andExpect(jsonPath("$.data.content[0].viewCount").value(0))
                    .andExpect(jsonPath("$.data.content[0].secret").value(false))
                    .andExpect(jsonPath("$.data.content[0].pinned").value(false))
                    .andExpect(jsonPath("$.data.content[0].commentCount").value(0))
                    .andDo(print());
        }

        @Test
        @WithMockUser
        void 비밀글_포함_게시글_검색() throws Exception{
            // given
            BoardSearchCondition condition = new BoardSearchCondition();
            ReflectionTestUtils.setField(condition, "keyword", "테스트");
            ReflectionTestUtils.setField(condition, "boardType", BoardType.NOTICE);
            ReflectionTestUtils.setField(condition, "searchType", BoardSearchType.TITLE);
            ReflectionTestUtils.setField(condition, "includeSecret", true);

            List<BoardSearchResult> content = List.of(
                    new BoardSearchResult(1L, "테스트 제목1", "testUser1",
                            0, true, false, LocalDateTime.now(), 0)
            );

            Page<BoardSearchResult> pageResult = new PageImpl<>(content);

            User mockUser = mock(User.class);
            when(mockUser.getRole()).thenReturn(UserRole.ROLE_ADMIN);
            UserDetailsImpl userDetails = new UserDetailsImpl(mockUser);

            when(boardService.searchBoardList(any(BoardSearchCondition.class), any(Pageable.class), any(User.class)))
                    .thenReturn(pageResult);

            // when & then
            mockMvc.perform(get("/library/board/search")
                            .with(user(userDetails))
                            .param("keyword", "테스트")
                            .param("boardType", BoardType.NOTICE.name())
                            .param("searchType", BoardSearchType.TITLE.name())
                            .param("includeSecret", "true"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.content[0].secret").value(true))
                    .andDo(print());
        }

        @Test
        @WithMockUser
        void 검색_결과_없어는_게시글_검색() throws Exception{
            // given
            BoardSearchCondition condition = new BoardSearchCondition();
            ReflectionTestUtils.setField(condition, "keyword", "존재하지않는키워드");
            ReflectionTestUtils.setField(condition, "boardType", BoardType.NOTICE);
            ReflectionTestUtils.setField(condition, "searchType", BoardSearchType.TITLE);
            ReflectionTestUtils.setField(condition, "includeSecret", false);

            Page<BoardSearchResult> pageResult = new PageImpl<>(Collections.emptyList());

            User mockUser = mock(User.class);
            when(mockUser.getRole()).thenReturn(UserRole.ROLE_USER);
            UserDetailsImpl userDetails = new UserDetailsImpl(mockUser);

            when(boardService.searchBoardList(any(BoardSearchCondition.class), any(Pageable.class), any(User.class)))
                    .thenReturn(pageResult);

            // when & then
            mockMvc.perform(get("/library/board/search")
                            .with(user(userDetails))
                            .param("keyword", "존재하지않는키워드")
                            .param("boardType", BoardType.NOTICE.name())
                            .param("searchType", BoardSearchType.TITLE.name())
                            .param("includeSecret", "false"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content.length()").value(0))
                    .andExpect(jsonPath("$.data.totalElements").value(0))
                    .andDo(print());
        }

        @Test
        @WithMockUser
        void 페이징_처리_확인하는_게시글_검색() throws Exception{
            // given
            int page = 0;
            int size = 10;
            List<BoardSearchResult> content = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();

            for (int i = 1; i <= size; i++) {
                content.add(new BoardSearchResult(
                        (long) i,                    // id
                        "테스트 제목" + i,            // title
                        "작성자" + i,                 // writerName
                        i,                          // viewCount
                        false,                      // isSecret
                        false,                      // isPinned
                        now.plusHours(i),           // createdAt
                        i                           // commentCount
                ));
            }

            Page<BoardSearchResult> pageResult = new PageImpl<>(content, PageRequest.of(page, size), 15);

            User mockUser = mock(User.class);
            when(mockUser.getRole()).thenReturn(UserRole.ROLE_USER);
            UserDetailsImpl userDetails = new UserDetailsImpl(mockUser);

            when(boardService.searchBoardList(any(BoardSearchCondition.class), any(Pageable.class), any(User.class)))
                    .thenReturn(pageResult);

            // when & then
            mockMvc.perform(get("/library/board/search")
                            .with(user(userDetails))
                            .param("keyword", "테스트")
                            .param("boardType", BoardType.NOTICE.name())
                            .param("searchType", BoardSearchType.TITLE.name())
                            .param("includeSecret", "false")
                            .param("page", String.valueOf(page))
                            .param("size", String.valueOf(size)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content.length()").value(size))
                    .andExpect(jsonPath("$.data.totalElements").value(15))
                    .andExpect(jsonPath("$.data.totalPages").value(2))
                    .andExpect(jsonPath("$.data.size").value(size))
                    .andExpect(jsonPath("$.data.number").value(page))
                    // 첫 번째 항목 상세 검증
                    .andExpect(jsonPath("$.data.content[0].id").value(1))
                    .andExpect(jsonPath("$.data.content[0].title").value("테스트 제목1"))
                    .andExpect(jsonPath("$.data.content[0].writerName").value("작성자1"))
                    .andExpect(jsonPath("$.data.content[0].viewCount").value(1))
                    .andExpect(jsonPath("$.data.content[0].secret").value(false))
                    .andExpect(jsonPath("$.data.content[0].pinned").value(false))
                    .andExpect(jsonPath("$.data.content[0].commentCount").value(1))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("게시글 수정 테스트")
    class updateBoardTest{
        @Test
        @WithMockUser
        void 게시글_수정_성공() throws Exception{
            // given
            Long boardId = 1L;
            BoardUpdateRequestDto requestDto = new BoardUpdateRequestDto();
            ReflectionTestUtils.setField(requestDto, "title", "수정된 제목");
            ReflectionTestUtils.setField(requestDto, "content", "수정된 내용");
            ReflectionTestUtils.setField(requestDto, "isSecret", true);
            ReflectionTestUtils.setField(requestDto, "isPinned", true);

            Board board = new Board(
                    "원본 제목",
                    "원본 내용",
                    false,
                    false,
                    BoardType.NOTICE,
                    mock(User.class)
            );
            board.partialUpdate(requestDto);
            BoardResponseDto responseDto = new BoardResponseDto(board);

            User mockUser = mock(User.class);
            when(mockUser.getRole()).thenReturn(UserRole.ROLE_ADMIN);
            UserDetailsImpl userDetails = new UserDetailsImpl(mockUser);

            when(boardService.updateBoard(eq(boardId), any(BoardUpdateRequestDto.class), any(User.class)))
                    .thenReturn(responseDto);

            // when & then
            mockMvc.perform(patch("/library/board/{boardId}", boardId)
                            .with(csrf())
                            .with(user(userDetails))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.title").value("수정된 제목"))
                    .andExpect(jsonPath("$.data.content").value("수정된 내용"))
                    .andExpect(jsonPath("$.data.secret").value(true))
                    .andExpect(jsonPath("$.data.pinned").value(true))
                    .andDo(print());
        }

        @Test
        @WithMockUser
        void 존재하지_읺는_게시글_수정_실패() throws Exception{
            // given
            Long boardId = 999L;
            BoardUpdateRequestDto requestDto = new BoardUpdateRequestDto();
            ReflectionTestUtils.setField(requestDto, "title", "수정된 제목");

            User mockUser = mock(User.class);
            when(mockUser.getRole()).thenReturn(UserRole.ROLE_USER);
            UserDetailsImpl userDetails = new UserDetailsImpl(mockUser);

            when(boardService.updateBoard(eq(boardId), any(BoardUpdateRequestDto.class), any(User.class)))
                    .thenThrow(new BoardNotFoundException());

            // when & then
            mockMvc.perform(patch("/library/board/{boardId}", boardId)
                            .with(csrf())
                            .with(user(userDetails))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value("ERROR"))
                    .andDo(print());
        }

        @Test
        @WithMockUser
        void 수정_권한이_없어서_게시글_수정_실패() throws Exception{
            // given
            Long boardId = 1L;
            BoardUpdateRequestDto requestDto = new BoardUpdateRequestDto();
            ReflectionTestUtils.setField(requestDto, "title", "수정된 제목");

            User mockUser = mock(User.class);
            when(mockUser.getRole()).thenReturn(UserRole.ROLE_USER);
            UserDetailsImpl userDetails = new UserDetailsImpl(mockUser);

            when(boardService.updateBoard(eq(boardId), any(BoardUpdateRequestDto.class), any(User.class)))
                    .thenThrow(new BoardAuthorityException());

            // when & then
            mockMvc.perform(patch("/library/board/{boardId}", boardId)
                            .with(csrf())
                            .with(user(userDetails))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.status").value("ERROR"))
                    .andDo(print());
        }

        @Test
        @WithMockUser
        void 제목만_수정하는_게시글_수정() throws Exception{
            // given
            Long boardId = 1L;
            BoardUpdateRequestDto requestDto = new BoardUpdateRequestDto();
            ReflectionTestUtils.setField(requestDto, "title", "수정된 제목");

            Board board = new Board(
                    "원본 제목",
                    "원본 내용",
                    false,
                    false,
                    BoardType.NOTICE,
                    mock(User.class)
            );
            board.partialUpdate(requestDto);
            BoardResponseDto responseDto = new BoardResponseDto(board);

            User mockUser = mock(User.class);
            when(mockUser.getRole()).thenReturn(UserRole.ROLE_ADMIN);
            UserDetailsImpl userDetails = new UserDetailsImpl(mockUser);

            when(boardService.updateBoard(eq(boardId), any(BoardUpdateRequestDto.class), any(User.class)))
                    .thenReturn(responseDto);

            // when & then
            mockMvc.perform(patch("/library/board/{boardId}", boardId)
                            .with(csrf())
                            .with(user(userDetails))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.title").value("수정된 제목"))
                    .andExpect(jsonPath("$.data.content").value("원본 내용"))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("게시글 삭제 테스트")
    class deleteBoardTest{
        @Test
        @WithMockUser
        void 게시글_삭제_성공() throws Exception{
            // given
            Long boardId = 1L;
            String expectedResponse = "게시글 삭제가 완료되었습니다.";

            User mockUser = mock(User.class);
            when(mockUser.getId()).thenReturn(1L);
            when(mockUser.getRole()).thenReturn(UserRole.ROLE_ADMIN);
            UserDetailsImpl userDetails = new UserDetailsImpl(mockUser);

            when(boardService.deleteBoard(eq(boardId), any(User.class)))
                    .thenReturn(expectedResponse);

            // when & then
            mockMvc.perform(delete("/library/board/{boardId}", boardId)
                            .with(csrf())
                            .with(user(userDetails)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"))
                    .andExpect(jsonPath("$.data").value(expectedResponse))
                    .andDo(print());
        }

        @Test
        @WithMockUser
        void 존재하지_않는_게시글_삭제시_실패() throws Exception{
            // given
            Long boardId = 999L;

            User mockUser = mock(User.class);
            when(mockUser.getRole()).thenReturn(UserRole.ROLE_USER);
            UserDetailsImpl userDetails = new UserDetailsImpl(mockUser);

            when(boardService.deleteBoard(eq(boardId), any(User.class)))
                    .thenThrow(new BoardNotFoundException());

            // when & then
            mockMvc.perform(delete("/library/board/{boardId}", boardId)
                            .with(csrf())
                            .with(user(userDetails)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value("ERROR"))
                    .andDo(print());
        }

        @Test
        @WithMockUser
        void 삭제_권한이_없어서_게시글_삭제_실패() throws Exception{
            // given
            Long boardId = 1L;

            User mockUser = mock(User.class);
            when(mockUser.getId()).thenReturn(2L);  // 다른 사용자의 ID
            when(mockUser.getRole()).thenReturn(UserRole.ROLE_USER);
            UserDetailsImpl userDetails = new UserDetailsImpl(mockUser);

            when(boardService.deleteBoard(eq(boardId), any(User.class)))
                    .thenThrow(new BoardAuthorityException());

            // when & then
            mockMvc.perform(delete("/library/board/{boardId}", boardId)
                            .with(csrf())
                            .with(user(userDetails)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.status").value("ERROR"))
                    .andDo(print());
        }

        @Test
        @WithMockUser
        void 이미_삭제된_게시글이라서_삭제_실패() throws Exception{
            // given
            Long boardId = 1L;

            User mockUser = mock(User.class);
            when(mockUser.getRole()).thenReturn(UserRole.ROLE_ADMIN);
            UserDetailsImpl userDetails = new UserDetailsImpl(mockUser);

            when(boardService.deleteBoard(eq(boardId), any(User.class)))
                    .thenThrow(new BoardNotFoundException());

            // when & then
            mockMvc.perform(delete("/library/board/{boardId}", boardId)
                            .with(csrf())
                            .with(user(userDetails)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value("ERROR"))
                    .andDo(print());
        }
    }
}