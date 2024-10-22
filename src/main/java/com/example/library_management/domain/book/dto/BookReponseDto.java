package com.example.library_management.domain.book.dto;

import com.example.library_management.domain.book.entity.Book;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class BookReponseDto {
    public BookReponseDto(Book book) {
        this.bookTitle = book.getBookTitle();
        this.bookDescription = book.getBookDescription();
        this.bookAuthor = book.getBookAuthor();
        this.bookPublisher = book.getBookPublisher();
        this.bookPublished = book.getBookPublished();
        this.category = book.getCategory().getCategoryName();
    }

    private String bookTitle;
    private String bookDescription;
    private String bookAuthor;
    private String bookPublisher;
    private LocalDate bookPublished;
    private String category;
}
