package com.example.library_management.domain.bookCopy.dto;


import com.example.library_management.domain.bookCopy.entity.BookCopy;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NONE // 타입 정보를 사용하지 않음
)
@Getter
@Setter
public class BookCopyResponseDto {
    private Long bookCopyId;
    private String BookTitle;

    public BookCopyResponseDto(Long bookCopyId, String booktitle) {
        this.bookCopyId = bookCopyId;
        BookTitle = booktitle;
    }
}
