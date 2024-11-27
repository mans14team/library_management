package com.example.library_management.domain.book.exception;

import com.example.library_management.domain.common.exception.GlobalException;
import com.example.library_management.domain.common.exception.GlobalExceptionConst;

public class BookException extends GlobalException {
    public BookException(GlobalExceptionConst globalExceptionConst) {
        super(globalExceptionConst);
    }
}