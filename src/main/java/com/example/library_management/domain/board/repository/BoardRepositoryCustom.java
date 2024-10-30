package com.example.library_management.domain.board.repository;

import com.example.library_management.domain.board.dto.request.BoardSearchCondition;
import com.example.library_management.domain.board.dto.response.BoardSearchResult;
import com.example.library_management.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardRepositoryCustom {
    Page<BoardSearchResult> search(BoardSearchCondition condition, User currentUser, Pageable pageable);
}
