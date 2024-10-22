package com.example.library_management.domain.auth.exception;

import com.example.library_management.domain.common.exception.GlobalException;

import static com.example.library_management.domain.common.exception.GlobalExceptionConst.DELETED_USER;

public class DeletedUserException extends GlobalException {
    public DeletedUserException() {
        super(DELETED_USER);
    }
}
