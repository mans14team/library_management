package com.example.library_management.domain.roomReserve.exception;

import com.example.library_management.domain.common.exception.GlobalException;

import static com.example.library_management.domain.common.exception.GlobalExceptionConst.ROOM_RESERVE_UNPROCESSABLE;

public class RoomReserveUnavailableException extends GlobalException {
    public RoomReserveUnavailableException() {
        super(ROOM_RESERVE_UNPROCESSABLE);
    }
}
