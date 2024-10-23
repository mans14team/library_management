package com.example.library_management.domain.boardComment.exception;

import com.example.library_management.domain.common.exception.GlobalException;

import static com.example.library_management.domain.common.exception.GlobalExceptionConst.NOT_FOUND_COMMENT;

public class CommentNouFoundException extends GlobalException {
    public CommentNouFoundException() {
        super(NOT_FOUND_COMMENT);
    }
}
