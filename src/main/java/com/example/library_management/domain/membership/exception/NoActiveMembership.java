package com.example.library_management.domain.membership.exception;

import com.example.library_management.domain.common.exception.GlobalException;

import static com.example.library_management.domain.common.exception.GlobalExceptionConst.NO_ACTIVE_MEMBERSHIP;

public class NoActiveMembership extends GlobalException {
    public NoActiveMembership() {
        super(NO_ACTIVE_MEMBERSHIP);
    }
}
