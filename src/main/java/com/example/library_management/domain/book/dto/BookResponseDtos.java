package com.example.library_management.domain.book.dto;

import com.example.library_management.domain.book.entity.Book;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookResponseDtos {
    public BookResponseDtos(Book book) {
        this.bookId = book.getId();
        this.bookTitle = book.getBookTitle();
        this.category = book.getCategory().getCategoryName();
    }

    private Long bookId;
    private String bookTitle;
    private String category;
}
