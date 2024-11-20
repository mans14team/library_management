package com.example.library_management.domain.review.exception;

import com.example.library_management.domain.common.exception.GlobalException;

import static com.example.library_management.domain.common.exception.GlobalExceptionConst.INVALID_REVIEWSTAR;

public class InvalidReviewStarException extends GlobalException {

    public InvalidReviewStarException(){
       super(INVALID_REVIEWSTAR);

    }
}
