package com.example.library_management.domain.bookCopy.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NONE // 타입 정보를 사용하지 않음
)
@Getter
@Setter
public class BookCopyRequestDto {
    private Long bookId;
    private LocalDate registeredAt;
}
