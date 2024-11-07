package com.example.library_management.domain.book.dto;

import com.example.library_management.domain.book.entity.Book;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class BookResponseDto {
    public BookResponseDto(Book book) {
        this.isbn = book.getIsbn();
        this.bookTitle = book.getBookTitle();
        this.bookPublished = book.getBookPublished();
        this.authors = book.getAuthors();
        this.publishers = book.getPublishers();
        this.subjects = book.getSubjects();
    }

    private String isbn;
    private String bookTitle;
    private Long bookPublished;
    private List<String> authors;
    private List<String> publishers;
    private List<String> subjects;
}
