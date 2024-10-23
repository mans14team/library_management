package com.example.library_management.domain.review.exception;

import com.example.library_management.domain.common.exception.GlobalException;

import static com.example.library_management.domain.common.exception.GlobalExceptionConst.UNAUTHORIZED_CREATE_REVIEW;

public class ReviewAccessForbiddenException extends GlobalException {

    public ReviewAccessForbiddenException(){
        super(UNAUTHORIZED_CREATE_REVIEW);
    }
}
