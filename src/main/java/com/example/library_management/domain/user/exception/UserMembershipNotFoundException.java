package com.example.library_management.domain.user.exception;

import com.example.library_management.domain.common.exception.GlobalException;

import static com.example.library_management.domain.common.exception.GlobalExceptionConst.NOT_FOUND_MEMBERSHIP;

public class UserMembershipNotFoundException extends GlobalException {
    public UserMembershipNotFoundException() {
        super(NOT_FOUND_MEMBERSHIP);
    }
}
