package com.example.library_management.domain.auth.exception;

import com.example.library_management.domain.common.exception.GlobalException;

import static com.example.library_management.domain.common.exception.GlobalExceptionConst.UNAUTHORIZED_PASSWORD;
public class UnauthorizedPasswordException extends GlobalException {
    public UnauthorizedPasswordException() {
        super(UNAUTHORIZED_PASSWORD);
    }
}
