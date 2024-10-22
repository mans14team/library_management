package com.example.library_management.domain.auth.exception;

import com.example.library_management.domain.common.exception.GlobalException;

import static com.example.library_management.domain.common.exception.GlobalExceptionConst.INVALID_PASSWORD;

public class InvalidPasswordFormatException extends GlobalException {
    public InvalidPasswordFormatException() {
        super(INVALID_PASSWORD);
    }
}
