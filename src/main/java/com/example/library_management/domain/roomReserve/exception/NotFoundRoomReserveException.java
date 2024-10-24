package com.example.library_management.domain.roomReserve.exception;

import com.example.library_management.domain.common.exception.GlobalException;

import static com.example.library_management.domain.common.exception.GlobalExceptionConst.NOT_FOUND_ROOM_RESERVE;

public class NotFoundRoomReserveException extends GlobalException{
    public NotFoundRoomReserveException() {
        super(NOT_FOUND_ROOM_RESERVE);
    }
}
