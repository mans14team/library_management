package com.example.library_management.domain.book.exception;

import org.springframework.http.HttpStatus;

public class CustomDuplicateException extends RuntimeException {
    public CustomDuplicateException(String isbn) {
        super(HttpStatus.BAD_REQUEST+ " 등록하려는 서적은 이미 등록된 서적입니다. : " + isbn + " 서적입니다.");
    }
}
