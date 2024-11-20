package com.example.library_management.domain.bookRental.exception;

import com.example.library_management.domain.common.exception.GlobalException;
import com.example.library_management.domain.common.exception.GlobalExceptionConst;

public class RentableException extends GlobalException {
    public RentableException() {super(GlobalExceptionConst.RENTAL_NOT_POSSIBLE);}
}

