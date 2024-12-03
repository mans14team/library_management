package com.example.library_management.domain.book.dto;

import com.example.library_management.domain.book.entity.Book;
import com.example.library_management.domain.book.entity.BookDocument;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BookResponseDto {
    private Long id;
    private String isbn;
    private String bookTitle;
    private String highlightedTitle; // 하이라이트된 제목
    private Long bookPublished;
    private List<String> authors;
    private List<String> highlightedAuthors; // 하이라이트된 저자 목록
    private List<String> publishers;
    private List<String> subjects;
    private Double score;  // 검색 점수

    public BookResponseDto(Book book) {
        this.id = book.getId();
        this.isbn = book.getIsbn();
        this.bookTitle = book.getBookTitle();
        this.bookPublished = book.getBookPublished();
        this.authors = book.getAuthors();
        this.publishers = book.getPublishers();
        this.subjects = book.getSubjects();
    }

    // 하이라이트 결과를 포함한 생성자
    public BookResponseDto(BookDocument doc, String highlightedTitle,
                           List<String> highlightedAuthors, float score) {
        this.id = Long.valueOf(doc.getId());
        this.isbn = doc.getIsbn();
        this.bookTitle = doc.getBookTitle();
        this.highlightedTitle = highlightedTitle;
        this.authors = doc.getAuthors();
        this.highlightedAuthors = highlightedAuthors;
        this.publishers = doc.getPublishers();
        this.subjects = doc.getSubjects();
        this.score = (double) score;
    }
}
