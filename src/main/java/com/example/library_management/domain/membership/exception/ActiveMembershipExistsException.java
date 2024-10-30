package com.example.library_management.domain.membership.exception;

import com.example.library_management.domain.common.exception.GlobalException;

import static com.example.library_management.domain.common.exception.GlobalExceptionConst.ACTIVE_MEMBERSHIP_EXISTS;

public class ActiveMembershipExistsException extends GlobalException {
    public ActiveMembershipExistsException() {
        super(ACTIVE_MEMBERSHIP_EXISTS);
    }
}
