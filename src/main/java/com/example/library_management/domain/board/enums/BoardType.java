package com.example.library_management.domain.board.enums;

import lombok.Getter;

@Getter
public enum BoardType {
    NOTICE("공지사항"),
    INQUIRY("문의사항");

    private final String value;

    BoardType(String value) {
        this.value = value;
    }
}
