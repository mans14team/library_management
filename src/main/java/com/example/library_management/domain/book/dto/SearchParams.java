package com.example.library_management.domain.book.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SearchParams {
    private String isbn;
    private String bookTitle;
    private String author;
    private String publisher;
    private List<String> subjects;
    private String searchTerm;
    private String sortField;
}
