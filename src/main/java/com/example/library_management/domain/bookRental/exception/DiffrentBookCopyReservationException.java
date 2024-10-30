package com.example.library_management.domain.bookRental.exception;

import org.springframework.http.HttpStatus;

public class DiffrentBookCopyReservationException extends RuntimeException {
    public DiffrentBookCopyReservationException(Long id) {
        super(HttpStatus.BAD_REQUEST+ " 대여 예약된 서적은 ID : " + id + " 서적입니다.");
    }
}
