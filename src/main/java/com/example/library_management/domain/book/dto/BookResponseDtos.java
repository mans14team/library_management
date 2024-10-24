package com.example.library_management.domain.book.dto;

import com.example.library_management.domain.book.entity.Book;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookResponseDtos {
    public BookResponseDtos(Book book) {
        this.bookTitle = book.getBookTitle();
        this.category = book.getCategory().getCategoryName();
    }

    private String bookTitle;
    private String category;
}
