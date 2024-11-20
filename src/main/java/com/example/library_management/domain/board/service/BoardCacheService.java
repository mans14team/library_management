package com.example.library_management.domain.board.service;

import com.example.library_management.domain.board.dto.response.BoardCacheDto;
import com.example.library_management.domain.board.dto.response.BoardListResponseDto;
import com.example.library_management.domain.board.enums.BoardStatus;
import com.example.library_management.domain.board.enums.BoardType;
import com.example.library_management.domain.board.repository.BoardRepository;
import com.example.library_management.domain.user.entity.User;
import com.example.library_management.domain.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BoardCacheService {
    private final BoardRepository boardRepository;

    @Cacheable(
            value = "boardList",
            key = "#boardType + '_' + #includeSecret + '_' + #user.role + '_' + #pageable.pageNumber"
    )
    public BoardCacheDto getCachedBoardList(BoardType boardType, boolean includeSecret,
                                            Pageable pageable, User user) {
        log.info("Cache Miss - Fetching from DB: boardType={}, includeSecret={}, page={}", boardType, includeSecret, pageable.getPageNumber());
        Page<BoardListResponseDto> pageResult = getBoardListFromDB(boardType, includeSecret, pageable, user);

        log.info("Caching data with key: boardList::" + boardType.name() + '_' + includeSecret + '_' + user.getRole() + '_' + pageable.getPageNumber());

        return new BoardCacheDto(
                pageResult.getContent(),
                pageResult.getTotalElements()
        );
    }

    private Page<BoardListResponseDto> getBoardListFromDB(BoardType boardType, boolean includeSecret, Pageable pageable, User user) {
        // 관리자인 경우
        if (user.getRole().equals(UserRole.ROLE_ADMIN)) {
            return boardRepository.findAllBoardDtoList(
                    boardType,
                    BoardStatus.ACTIVE,
                    pageable
            );
        }// 일반 사용자이고 비밀글 포함 요청인 경우
        else if (includeSecret) {
            return boardRepository.findBoardDtoListForUser(
                    boardType,
                    BoardStatus.ACTIVE,
                    user,
                    pageable
            );
        }// 일반 사용자이고 공개글만 요청하는 경우
        else {
            return boardRepository.findBoardDtoList(
                    boardType,
                    BoardStatus.ACTIVE,
                    pageable
            );
        }
    }
}
