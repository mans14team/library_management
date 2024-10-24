package com.example.library_management.domain.bookRental.controller;

import com.example.library_management.domain.bookRental.dto.BookRentalRequestDto;
import com.example.library_management.domain.bookRental.dto.BookRentalResponseDto;
import com.example.library_management.domain.bookRental.entity.BookRental;
import com.example.library_management.domain.bookRental.service.BookRentalService;
import com.example.library_management.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rental")
public class bookRentalController {
    private final BookRentalService bookRentalService;

    @PostMapping
    public ResponseEntity<BookRentalResponseDto> submitBookRental(@RequestBody BookRentalRequestDto bookRentalRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        BookRentalResponseDto bookRentalResponseDto = bookRentalService.submitBookRental(bookRentalRequestDto, userDetails);
        return ResponseEntity.ok(bookRentalResponseDto);
    }

    @PutMapping
    public ResponseEntity<BookRentalResponseDto> returnBookRental(@RequestBody BookRentalRequestDto bookRentalRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        BookRentalResponseDto bookRentalResponseDto = bookRentalService.returnBookRental(bookRentalRequestDto, userDetails);
        return ResponseEntity.ok(bookRentalResponseDto);
    }
}
