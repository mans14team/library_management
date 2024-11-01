package com.example.library_management.domain.roomReserve.exception;

import com.example.library_management.domain.common.exception.GlobalException;

import static com.example.library_management.domain.common.exception.GlobalExceptionConst.FORBIDDEN_RESERVATION_DELETE;

public class ReservationDeleteNotAllowedException extends GlobalException {
    public ReservationDeleteNotAllowedException() {
        super(FORBIDDEN_RESERVATION_DELETE);
    }
}
