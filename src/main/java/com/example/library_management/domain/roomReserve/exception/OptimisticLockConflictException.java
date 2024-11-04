package com.example.library_management.domain.roomReserve.exception;

import com.example.library_management.domain.common.exception.GlobalException;

import static com.example.library_management.domain.common.exception.GlobalExceptionConst.OPTIMISTIC_LOCK_CONFLICT;

public class OptimisticLockConflictException extends GlobalException {
    public OptimisticLockConflictException() {
        super(OPTIMISTIC_LOCK_CONFLICT);
    }
}
