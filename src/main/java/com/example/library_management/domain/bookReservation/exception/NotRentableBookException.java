package com.example.library_management.domain.bookReservation.exception;

import com.example.library_management.domain.common.exception.GlobalException;
import com.example.library_management.domain.common.exception.GlobalExceptionConst;

public class NotRentableBookException extends GlobalException {
    public NotRentableBookException() {
        super(GlobalExceptionConst.NOT_FOUND_RENTABLE_BOOKCOPY);
    }
}
