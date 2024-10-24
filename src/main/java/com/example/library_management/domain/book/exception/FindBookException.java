package com.example.library_management.domain.book.exception;

import com.example.library_management.domain.common.exception.GlobalException;
import com.example.library_management.domain.common.exception.GlobalExceptionConst;

public class FindBookException extends GlobalException {
    public FindBookException() {
        super(GlobalExceptionConst.NOT_FOUND_BOOK);
    }
}
