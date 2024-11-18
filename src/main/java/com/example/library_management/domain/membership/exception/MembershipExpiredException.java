package com.example.library_management.domain.membership.exception;

import com.example.library_management.domain.common.exception.GlobalException;

import static com.example.library_management.domain.common.exception.GlobalExceptionConst.MEMBERSHIP_EXPIRED;

public class MembershipExpiredException extends GlobalException {
    public MembershipExpiredException() {
        super(MEMBERSHIP_EXPIRED);
    }
}
