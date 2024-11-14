package com.example.library_management.domain.bookRental.controller;

import com.example.library_management.domain.bookRental.dto.BookRentalRequestDto;
import com.example.library_management.domain.bookRental.dto.BookRentalResponseDto;
import com.example.library_management.domain.bookRental.exception.DiffrentBookCopyReservationException;
import com.example.library_management.domain.bookRental.service.BookRentalService;
import com.example.library_management.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rental")
public class BookRentalController {
    private final BookRentalService bookRentalService;

    @PostMapping
    public ResponseEntity<BookRentalResponseDto> submitBookRental(@RequestBody BookRentalRequestDto bookRentalRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) throws DiffrentBookCopyReservationException {
        BookRentalResponseDto bookRentalResponseDto = bookRentalService.submitBookRental(bookRentalRequestDto, userDetails);
        return ResponseEntity.ok(bookRentalResponseDto);
    }

    @PutMapping
    public ResponseEntity<BookRentalResponseDto> returnBookRental(@RequestBody BookRentalRequestDto bookRentalRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        BookRentalResponseDto bookRentalResponseDto = bookRentalService.returnBookRental(bookRentalRequestDto, userDetails);
        return ResponseEntity.ok(bookRentalResponseDto);
    }

    @DeleteMapping("/{book_rental_id}")
    public ResponseEntity<Long> deleteBookRental(@PathVariable("book_rental_id") Long bookRentalId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long deletedBookRental = bookRentalService.deleteBookRental(bookRentalId, userDetails);
        return ResponseEntity.ok(deletedBookRental);
    }

    // 현재 로그인한 유저의 대여 기록 조회
    @GetMapping("/me")
    public ResponseEntity<List<BookRentalResponseDto>> getBookRental(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<BookRentalResponseDto> bookRentalResponseDtoList = bookRentalService.getBookRental(userDetails);
        return ResponseEntity.ok(bookRentalResponseDtoList);
    }

    @GetMapping
    public ResponseEntity<List<BookRentalResponseDto>> getAllBookRental(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<BookRentalResponseDto> bookRentalResponseDtoList = bookRentalService.getAllBookRental(userDetails);
        return ResponseEntity.ok(bookRentalResponseDtoList);
    }
}
