package com.example.library_management.domain.roomReserve.exception;

import com.example.library_management.domain.common.exception.GlobalException;

import static com.example.library_management.domain.common.exception.GlobalExceptionConst.REQUEST_LOCK_TIME_OUT;

public class ReservationLockTimeOutException extends GlobalException {
    public ReservationLockTimeOutException() {
        super(REQUEST_LOCK_TIME_OUT);
    }
}
