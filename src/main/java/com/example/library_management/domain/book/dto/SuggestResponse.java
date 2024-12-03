package com.example.library_management.domain.book.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SuggestResponse {
    private Long id;              // 도서 ID 추가
    private String text; // 검색어와 매칭된 텍스트
    private String type;  // TITLE, AUTHOR 등 구분
    private Double score; // 검색 점수
    private String bookTitle;  // 책 제목
    private List<String> authors;   // 저자 목록

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SuggestResponse that = (SuggestResponse) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(text, that.text) &&
                Objects.equals(type, that.type) &&
                Objects.equals(score, that.score) &&
                Objects.equals(bookTitle, that.bookTitle) &&
                Objects.equals(authors, that.authors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, type, score, bookTitle, authors);
    }
}
