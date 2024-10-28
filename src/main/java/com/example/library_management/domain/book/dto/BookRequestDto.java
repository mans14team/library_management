package com.example.library_management.domain.book.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class BookRequestDto {
    private String bookTitle;
    private String bookDescription;
    private String bookAuthor;
    private String bookPublisher;
    private LocalDate bookPublished;
    private Long categoryId;
}
