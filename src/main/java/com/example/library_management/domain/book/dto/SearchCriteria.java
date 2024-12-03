package com.example.library_management.domain.book.dto;

import com.example.library_management.domain.book.enums.SearchType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class SearchCriteria {
    private SearchType searchType;
    private String isbn;
    private String bookTitle;
    private String author;
    private String publisher;
    private List<String> subjects;
    private String searchTerm;
    private String sortField;
}
