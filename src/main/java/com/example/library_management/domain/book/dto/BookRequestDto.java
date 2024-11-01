package com.example.library_management.domain.book.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Year;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class BookRequestDto {

    private String isbn;
    private String bookTitle;
    private Long bookPublished;
    private Set<String> bookAuthor;
    private Set<String> bookPublisher;
    private Set<String> bookSubject;
}
