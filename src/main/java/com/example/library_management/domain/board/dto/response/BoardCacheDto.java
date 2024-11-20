package com.example.library_management.domain.board.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;

import java.util.List;

@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class BoardCacheDto {
    private List<BoardListResponseDto> content;
    private long totalElements;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public BoardCacheDto(
            @JsonProperty("content") List<BoardListResponseDto> content,
            @JsonProperty("totalElements") long totalElements
    ) {
        this.content = content;
        this.totalElements = totalElements;
    }
}
