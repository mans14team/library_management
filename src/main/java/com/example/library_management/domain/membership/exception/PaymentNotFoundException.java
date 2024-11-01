package com.example.library_management.domain.membership.exception;

import com.example.library_management.domain.common.exception.GlobalException;

import static com.example.library_management.domain.common.exception.GlobalExceptionConst.NOT_FOUND_PAYMENT;

public class PaymentNotFoundException extends GlobalException {
    public PaymentNotFoundException() {
        super(NOT_FOUND_PAYMENT);
    }
}
