package com.example.library_management.domain.book.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SuggestResponse {
    private String text;
    private String type; // TITLE, AUTHOR 등 구분
    private Double score;
    private String originalText;  // 원본 텍스트 (제목 또는 저자)

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SuggestResponse that = (SuggestResponse) o;
        return Objects.equals(text, that.text) &&
                Objects.equals(type, that.type) &&
                Objects.equals(originalText, that.originalText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, type, originalText);
    }
}
