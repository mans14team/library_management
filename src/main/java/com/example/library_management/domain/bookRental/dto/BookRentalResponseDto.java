package com.example.library_management.domain.bookRental.dto;

import com.example.library_management.domain.bookRental.entity.BookRental;
import com.example.library_management.domain.bookRental.enums.RentalState;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookRentalResponseDto {
    private Long bookCopyId;
    private String bookTitle;
    private Long userId;
    private LocalDateTime rentalDate;
    private LocalDateTime returnDate;
    private RentalState rentalState;

    public BookRentalResponseDto(BookRental bookRental) {
        this.bookCopyId = bookRental.getBookCopy().getId();
        this.bookTitle = bookRental.getBookCopy().getBook().getBookTitle();
        this.userId = bookRental.getUser().getId();
        this.rentalDate = bookRental.getRentalDate();
        this.returnDate = bookRental.getReturnDate();
        this.rentalState = bookRental.getRentalState();
    }
}
