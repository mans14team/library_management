package com.example.library_management.domain.book.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;

import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NONE // 타입 정보를 사용하지 않음
)
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
