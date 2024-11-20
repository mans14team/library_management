package com.example.library_management.domain.membership.exception;

import com.example.library_management.domain.common.exception.GlobalException;

import static com.example.library_management.domain.common.exception.GlobalExceptionConst.PAYMENT_SERVER_ERROR;

public class PaymentServerException extends GlobalException {
    public PaymentServerException() {
        super(PAYMENT_SERVER_ERROR);
    }
}
