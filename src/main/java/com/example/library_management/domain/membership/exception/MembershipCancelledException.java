package com.example.library_management.domain.membership.exception;

import com.example.library_management.domain.common.exception.GlobalException;

import static com.example.library_management.domain.common.exception.GlobalExceptionConst.MEMBERSHIP_CANCELLED;

public class MembershipCancelledException extends GlobalException {
    public MembershipCancelledException() {
        super(MEMBERSHIP_CANCELLED);
    }
}
