package com.example.library_management.domain.common.exception;

import com.example.library_management.domain.bookRental.exception.DiffrentBookCopyReservationException;
import com.example.library_management.domain.common.dto.ErrorResponse;
import com.example.library_management.global.config.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    
    // GlobalException 처리
    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleGlobalException(GlobalException e){
        e.printStackTrace();
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(ApiResponse.error(new ErrorResponse(e.getHttpStatus().value(), e.getMessage())));
    }

    // RuntimeException 처리
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleRuntimeException(RuntimeException e) {
        e.printStackTrace();
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

    // PreAuthorize 권한 체크시 오류 처리
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleAuthorizationDeniedException(AuthorizationDeniedException e) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN.value(), "접근 권한이 없습니다.");

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(errorResponse));
    }
}
