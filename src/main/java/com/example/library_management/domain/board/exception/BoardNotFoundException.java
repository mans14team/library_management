package com.example.library_management.domain.board.exception;

import com.example.library_management.domain.common.exception.GlobalException;

import static com.example.library_management.domain.common.exception.GlobalExceptionConst.NOT_FOUND_BOARD;

public class BoardNotFoundException extends GlobalException {
    public BoardNotFoundException() {
        super(NOT_FOUND_BOARD);
    }
}
