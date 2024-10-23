package com.example.library_management.domain.boardComment.repository;

import com.example.library_management.domain.boardComment.entity.BoardComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardCommentRepository extends JpaRepository<BoardComment, Long> {
}
