package com.example.library_management.domain.book.dto;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class BookRequestDto {
    private String bookTitle;
    private String bookDescription;
    private String bookAuthor;
    private String bookPublisher;
    private LocalDate bookPublished;
    private Long categoryId;
}
