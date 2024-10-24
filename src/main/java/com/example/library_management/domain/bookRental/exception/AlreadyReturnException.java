package com.example.library_management.domain.bookRental.exception;

import com.example.library_management.domain.common.exception.GlobalException;
import com.example.library_management.domain.common.exception.GlobalExceptionConst;

public class AlreadyReturnException extends GlobalException {
    public AlreadyReturnException() {super(GlobalExceptionConst.ALREADY_RETURN);}
}
