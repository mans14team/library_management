package com.example.library_management.domain.review.exception;

import com.example.library_management.domain.common.exception.GlobalException;

import static com.example.library_management.domain.common.exception.GlobalExceptionConst.FORBIDDEN_CREATE_REVIEW;

public class ReviewAccessForbiddenException extends GlobalException {

    public ReviewAccessForbiddenException(){
        super(FORBIDDEN_CREATE_REVIEW);
    }
}
