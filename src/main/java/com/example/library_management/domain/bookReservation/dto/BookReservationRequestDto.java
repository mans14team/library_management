package com.example.library_management.domain.bookReservation.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class BookReservationRequestDto {
    private Long bookId;
    private LocalDate reservationDate;
}
