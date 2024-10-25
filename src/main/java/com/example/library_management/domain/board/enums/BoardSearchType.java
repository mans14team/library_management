package com.example.library_management.domain.board.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BoardSearchType {
    TITLE(SearchType.TITLE),           // 제목으로 검색
    CONTENT(SearchType.CONTENT),       // 내용으로 검색
    WRITER(SearchType.WRITER),         // 작성자로 검색
    ALL(SearchType.ALL);              // 제목 + 내용으로 검색

    private final String searchType;

    public static class SearchType {
        public static final String TITLE = "TITLE";
        public static final String CONTENT = "CONTENT";
        public static final String WRITER = "WRITER";
        public static final String ALL = "ALL";
    }
}
