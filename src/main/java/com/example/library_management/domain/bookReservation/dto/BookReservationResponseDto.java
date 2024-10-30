package com.example.library_management.domain.bookReservation.dto;

import com.example.library_management.domain.bookReservation.entity.BookReservation;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BookReservationResponseDto {
    private Long bookCopyId;
    private LocalDate reservationDate;

    public BookReservationResponseDto(BookReservation bookReservation) {
        this.bookCopyId = bookReservation.getBookCopy().getId();
        this.reservationDate = bookReservation.getReservationDate();
    }
}
