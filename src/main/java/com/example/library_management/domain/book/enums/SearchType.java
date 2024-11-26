package com.example.library_management.domain.book.enums;

public enum SearchType {
    ISBN,           // ISBN 검색
    FUZZY,          // 퍼지 검색 (오타 허용)
    COMPREHENSIVE,  // 전체 필드 검색
    SUBJECT,        // 주제 검색
    DEFAULT         // 기본 검색
}
