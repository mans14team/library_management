package com.example.library_management.domain.bookCopy.exception;

import com.example.library_management.domain.common.exception.GlobalException;
import com.example.library_management.domain.common.exception.GlobalExceptionConst;

public class FindBookCopyException extends GlobalException {
    public FindBookCopyException() {
        super(GlobalExceptionConst.NOT_FOUND_BOOK_COPY);
    }
}
