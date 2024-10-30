package com.example.library_management.domain.membership.exception;

import com.example.library_management.domain.common.exception.GlobalException;

import static com.example.library_management.domain.common.exception.GlobalExceptionConst.DUPLICATE_PAYMENT;

public class DuplicatePaymentException extends GlobalException {
    public DuplicatePaymentException() {
        super(DUPLICATE_PAYMENT);
    }
}
