package com.example.library_management.domain.room.exception;

import com.example.library_management.domain.common.exception.GlobalException;
import com.example.library_management.domain.common.exception.GlobalExceptionConst;

import static com.example.library_management.domain.common.exception.GlobalExceptionConst.NOT_FOUND_ROOM;

public class NotFoundRoomException extends GlobalException {
    public NotFoundRoomException() {
        super(NOT_FOUND_ROOM);
    }
}
