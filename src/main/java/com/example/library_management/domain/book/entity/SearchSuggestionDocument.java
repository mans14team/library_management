package com.example.library_management.domain.book.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Document(indexName = "search_suggestions")
@Getter
@NoArgsConstructor
public class SearchSuggestionDocument {
    @Id
    private String id;

    // 검색어
    @Field(type = FieldType.Keyword)
    private String searchTerm;

    // 연관 검색어와 그 가중치 (점수)를 저장하는 맵
    @Field(type = FieldType.Object)
    private Map<String, Double> relatedTerms = new HashMap<>();

    // 검색어가 사용된 횟수
    @Field(type = FieldType.Long)
    private long searchCount;

    // 이 검색어로 도서가 선택된 횟수
    @Field(type = FieldType.Long)
    private long clickCount;

    // 마지막 검색 시간
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime lastSearched;

    // 연관된 도서 ID들과 그 가중치
    @Field(type = FieldType.Object)
    private Map<String, Double> relatedBookIds = new HashMap<>();

    public SearchSuggestionDocument(String searchTerm) {
        this.searchTerm = searchTerm;
        this.searchCount = 1;
        this.clickCount = 0;
        this.lastSearched = LocalDateTime.now();
    }

    // 검색 횟수 증가
    public void incrementSearchCount() {
        this.searchCount++;
        this.lastSearched = LocalDateTime.now();
    }

    // 클릭 횟수 증가
    public void incrementClickCount() {
        this.clickCount++;
    }

    // 연관 검색어 추가 또는 업데이트
    public void updateRelatedTerm(String term, Double score) {
        relatedTerms.merge(term, score, Double::sum);
    }

    // 연관 도서 추가 또는 업데이트
    public void updateRelatedBook(String bookId, Double score) {
        relatedBookIds.merge(bookId, score, Double::sum);
    }
}