package com.example.library_management.domain.book.exception;

import com.example.library_management.domain.common.exception.GlobalException;
import com.example.library_management.domain.common.exception.GlobalExceptionConst;

public class FindCatogoryException extends GlobalException {
    public FindCatogoryException() {
        super(GlobalExceptionConst.NOT_FOUND_CATEGORY);
    }
}
