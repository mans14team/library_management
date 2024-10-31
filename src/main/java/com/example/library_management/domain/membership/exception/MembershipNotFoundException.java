package com.example.library_management.domain.membership.exception;

import com.example.library_management.domain.common.exception.GlobalException;

import static com.example.library_management.domain.common.exception.GlobalExceptionConst.NOT_FOUND_MEMBERSHIP;

public class MembershipNotFoundException extends GlobalException {
    public MembershipNotFoundException() {
        super(NOT_FOUND_MEMBERSHIP);
    }
}
