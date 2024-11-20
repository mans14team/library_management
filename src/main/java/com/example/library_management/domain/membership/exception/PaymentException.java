package com.example.library_management.domain.membership.exception;

import com.example.library_management.domain.common.exception.GlobalException;
import com.example.library_management.domain.common.exception.GlobalExceptionConst;

public class PaymentException extends GlobalException {
    public PaymentException(GlobalExceptionConst globalExceptionConst) {
        super(globalExceptionConst);
    }
}
