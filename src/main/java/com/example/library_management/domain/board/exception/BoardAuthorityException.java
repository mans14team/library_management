package com.example.library_management.domain.board.exception;

import com.example.library_management.domain.common.exception.GlobalException;

import static com.example.library_management.domain.common.exception.GlobalExceptionConst.UNAUTHORIZED_CREATE;

public class BoardAuthorityException extends GlobalException {
    public BoardAuthorityException() {
        super(UNAUTHORIZED_CREATE);
    }
}
