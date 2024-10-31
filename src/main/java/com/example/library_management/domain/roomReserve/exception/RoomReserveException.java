package com.example.library_management.domain.roomReserve.exception;

import com.example.library_management.domain.common.exception.GlobalException;

import static com.example.library_management.domain.common.exception.GlobalExceptionConst.ROOM_RESERVE_EXCEPTION;

public class RoomReserveException extends GlobalException {
    public RoomReserveException() {
        super(ROOM_RESERVE_EXCEPTION);
    }
}
