package com.example.library_management.domain.book.dto;

import com.example.library_management.domain.book.entity.Book;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookResponseDtos {
    public BookResponseDtos(Book book) {
        this.bookId = book.getId();
        this.isbn = book.getIsbn();
        this.bookTitle = book.getBookTitle();
    }

    private Long bookId;
    private String isbn;
    private String bookTitle;
}
