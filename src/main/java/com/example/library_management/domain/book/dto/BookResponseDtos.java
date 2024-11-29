package com.example.library_management.domain.book.dto;

import com.example.library_management.domain.book.entity.Book;
import com.example.library_management.domain.book.entity.BookDocument;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookResponseDtos {
    private Long id;
    private String bookTitle;
    private String highlightedTitle;  // 하이라이트된 제목
    private List<String> authors;
    private List<String> highlightedAuthors;  // 하이라이트된 저자
    private Double score;  // 검색 점수
    private Long bookPublished;  // 출간년도

    public BookResponseDtos(Book book) {
        this.id = book.getId();
        this.bookTitle = book.getBookTitle();
        this.authors = book.getAuthors();
    }

    // 검색 결과용 생성자
    public BookResponseDtos(BookDocument doc, String highlightedTitle, List<String> highlightedAuthors, float score) {
        this.id = Long.valueOf(doc.getId());
        this.bookTitle = doc.getBookTitle();
        this.authors = doc.getAuthors();
        this.highlightedTitle = highlightedTitle;
        this.highlightedAuthors = highlightedAuthors;
        this.score = (double) score;
    }

    // 하이라이트가 없는 검색 결과용 생성자
    public BookResponseDtos(BookDocument doc) {
        this.id = Long.valueOf(doc.getId());
        this.bookTitle = doc.getBookTitle();
        this.authors = doc.getAuthors();
    }
}
