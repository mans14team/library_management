package com.example.library_management.domain.room.exception;

import com.example.library_management.domain.common.exception.GlobalException;

import static com.example.library_management.domain.common.exception.GlobalExceptionConst.UNAUTHORIZED_ROOMCONTROL;

public class UnauthorizedRoomAccessException extends GlobalException {
    public UnauthorizedRoomAccessException() {
        super(UNAUTHORIZED_ROOMCONTROL);
    }
}
