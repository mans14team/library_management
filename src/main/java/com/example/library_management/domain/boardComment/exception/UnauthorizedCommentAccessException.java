package com.example.library_management.domain.boardComment.exception;

import com.example.library_management.domain.common.exception.GlobalException;

import static com.example.library_management.domain.common.exception.GlobalExceptionConst.UNAUTHORIZED_COMMENT_ACCESS;

public class UnauthorizedCommentAccessException extends GlobalException {
    public UnauthorizedCommentAccessException() {
        super(UNAUTHORIZED_COMMENT_ACCESS);
    }
}
