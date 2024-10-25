package com.example.library_management.domain.boardComment.exception;

import com.example.library_management.domain.common.exception.GlobalException;

import static com.example.library_management.domain.common.exception.GlobalExceptionConst.MISMATCHED_COMMENT_BOARD;

public class CommentMismatchException extends GlobalException {
    public CommentMismatchException() {
        super(MISMATCHED_COMMENT_BOARD);
    }
}
