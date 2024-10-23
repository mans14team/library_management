package com.example.library_management.domain.board.enums;

import lombok.Getter;

@Getter
public enum BoardStatus {
    // 공지사항 상태
    ACTIVE("활성"),
    INACTIVE("비활성");

    private final String value;

    BoardStatus(String value) {
        this.value = value;
    }
}
