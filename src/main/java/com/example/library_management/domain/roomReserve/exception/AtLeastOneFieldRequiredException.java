package com.example.library_management.domain.roomReserve.exception;

import com.example.library_management.domain.common.exception.GlobalException;

import static com.example.library_management.domain.common.exception.GlobalExceptionConst.INSUFFICIENT_DATA_DELIVERED;

public class AtLeastOneFieldRequiredException extends GlobalException {
    public AtLeastOneFieldRequiredException() {
        super(INSUFFICIENT_DATA_DELIVERED);
    }
}
