package com.example.library_management.domain.bookRental.dto;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class BookRentalRequestDto {
    private Long bookCopyId;
    private Long userId;
}
