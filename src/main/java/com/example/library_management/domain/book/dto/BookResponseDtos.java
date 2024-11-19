package com.example.library_management.domain.book.dto;

import com.example.library_management.domain.book.entity.Book;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NONE // 타입 정보를 사용하지 않음
)
@Getter
@Setter
public class BookResponseDtos {
    public BookResponseDtos(Book book) {
        this.bookId = book.getId();
        this.bookTitle = book.getBookTitle();
    }

    private Long bookId;
    private String bookTitle;
}
