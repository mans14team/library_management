package com.example.library_management.domain.bookReservation.exception;

import com.example.library_management.domain.common.exception.GlobalException;
import com.example.library_management.domain.common.exception.GlobalExceptionConst;

public class FindBookReservationException extends GlobalException {
    public FindBookReservationException() {
        super(GlobalExceptionConst.NOT_FOUND_BOOK_RESERVATION);
    }
}
