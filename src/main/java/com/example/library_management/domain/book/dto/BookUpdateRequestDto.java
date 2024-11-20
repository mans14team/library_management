package com.example.library_management.domain.book.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class BookUpdateRequestDto {
    private String bookTitle;
    private Long bookPublished;

    private List<String> addAuthors;
    private List<String> addPublishers;
    private List<String> addSubjects;

    private List<String> removeAuthors;
    private List<String> removePublishers;
    private List<String> removeSubjects;
}
