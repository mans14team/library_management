package com.example.library_management.domain.book.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NONE // 타입 정보를 사용하지 않음
)
@Getter
@Setter
public class BookRequestDto {
    private String isbn;
    private String bookTitle;
    private Long bookPublished;
    private List<String> authors;
    private List<String> publishers;
    private List<String> subjects;
}
