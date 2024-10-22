package com.example.library_management.domain.auth.exception;

import com.example.library_management.domain.common.exception.GlobalException;

import static com.example.library_management.domain.common.exception.GlobalExceptionConst.DUPLICATE_EMAIL;
public class DuplicateEmailException extends GlobalException {
    public DuplicateEmailException() {
        super(DUPLICATE_EMAIL);
    }
}
