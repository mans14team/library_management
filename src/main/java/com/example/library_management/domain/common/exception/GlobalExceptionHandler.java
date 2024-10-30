package com.example.library_management.domain.common.exception;

import com.example.library_management.domain.bookRental.exception.DiffrentBookCopyReservationException;
import com.example.library_management.domain.common.dto.ErrorResponse;
import com.example.library_management.global.config.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    
    // GlobalException 처리
    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleGlobalException(GlobalException e){
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(ApiResponse.error(new ErrorResponse(e.getHttpStatus().value(), e.getMessage())));
    }

    // RuntimeException 처리
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleRuntimeException(RuntimeException e) {
        ErrorResponse errorResponse = new ErrorResponse(500, e.getMessage());
        return ResponseEntity
                .status(500)
                .body(ApiResponse.error(errorResponse));
    }

    @ExceptionHandler(DiffrentBookCopyReservationException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleDiffrentBookCopyReservationException(DiffrentBookCopyReservationException e) {
        ErrorResponse errorResponse = new ErrorResponse(400, e.getMessage());
        return ResponseEntity
                .status(400)
                .body(ApiResponse.error(errorResponse));
    }
}
