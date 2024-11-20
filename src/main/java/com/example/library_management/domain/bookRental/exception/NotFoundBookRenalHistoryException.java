package com.example.library_management.domain.bookRental.exception;

import com.example.library_management.domain.common.exception.GlobalException;
import com.example.library_management.domain.common.exception.GlobalExceptionConst;

public class NotFoundBookRenalHistoryException extends GlobalException {
    public NotFoundBookRenalHistoryException() {super(GlobalExceptionConst.NOT_FOUND_BOOK_RENTAL);}
}
