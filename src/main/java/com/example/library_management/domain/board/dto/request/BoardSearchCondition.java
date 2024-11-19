package com.example.library_management.domain.board.dto.request;

import com.example.library_management.domain.board.enums.BoardSearchType;
import com.example.library_management.domain.board.enums.BoardType;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NONE // 타입 정보를 사용하지 않음
)
@Getter
public class BoardSearchCondition {
    private String keyword;          // 검색어
    private BoardSearchType searchType;   // 검색 유형
    private BoardType boardType;     // 게시글 유형
    private boolean includeSecret;   // 비밀글 포함 여부
}
