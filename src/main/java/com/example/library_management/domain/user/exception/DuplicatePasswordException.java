package com.example.library_management.domain.user.exception;

import com.example.library_management.domain.common.exception.GlobalException;

import static com.example.library_management.domain.common.exception.GlobalExceptionConst.DUPLICATE_PASSWORD;

public class DuplicatePasswordException extends GlobalException {
    public DuplicatePasswordException() {
        super(DUPLICATE_PASSWORD);
    }
}
