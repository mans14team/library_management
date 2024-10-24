package com.example.library_management.domain.book.exception;

import com.example.library_management.domain.common.exception.GlobalException;
import com.example.library_management.domain.common.exception.GlobalExceptionConst;

public class AuthorizedAdminException extends GlobalException {
    public AuthorizedAdminException() {
        super(GlobalExceptionConst.UNAUTHORIZED_ADMIN);
    }
}
