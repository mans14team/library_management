package com.example.library_management.domain.roomReserve.exception;

import com.example.library_management.domain.common.exception.GlobalException;

import static com.example.library_management.domain.common.exception.GlobalExceptionConst.FORBIDDEN_RESERVATION_MODIFICATION;

public class ReservationModificationNotAllowedException extends GlobalException {
    public ReservationModificationNotAllowedException() {
        super(FORBIDDEN_RESERVATION_MODIFICATION);
    }
}
