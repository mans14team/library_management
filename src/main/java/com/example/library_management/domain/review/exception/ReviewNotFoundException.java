package com.example.library_management.domain.review.exception;

import com.example.library_management.domain.common.exception.GlobalException;
import com.example.library_management.domain.common.exception.GlobalExceptionConst;

public class ReviewNotFoundException extends GlobalException {

    public ReviewNotFoundException() {
        super(GlobalExceptionConst.NOT_FOUND_REVIEW);
    }
}
