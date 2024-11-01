package com.example.library_management.domain.book.dto;

import com.example.library_management.domain.book.entity.Book;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class BookResponseDto {
    public BookResponseDto(Book book) {
        this.isbn = book.getIsbn();
        this.bookTitle = book.getBookTitle();
        this.bookPublished = book.getBookPublished();
        this.authors = book.getAuthors().stream().map(a -> a.getAuthorName()).toList();
    }

    private String isbn;
    private String bookTitle;
    private Long bookPublished;
    private List<String> authors;
//    private List<String> publishers;
//    private List<String> subjects;
}
