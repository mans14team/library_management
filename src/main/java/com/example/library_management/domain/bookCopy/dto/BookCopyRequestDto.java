package com.example.library_management.domain.bookCopy.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class BookCopyRequestDto {
    private Long bookId;
    private LocalDate registeredAt;
}
