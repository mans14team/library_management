package com.example.library_management.domain.roomReserve.exception;

import com.example.library_management.domain.common.exception.GlobalException;

import static com.example.library_management.domain.common.exception.GlobalExceptionConst.UNAUTHORIZED_RESERVATION_MODIFICATION;

public class ReservationModificationNotAllowedException extends GlobalException {
    public ReservationModificationNotAllowedException() {
        super(UNAUTHORIZED_RESERVATION_MODIFICATION);
    }
}
