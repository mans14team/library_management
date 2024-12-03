package com.example.library_management.domain.book.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RelatedSearchResponse {
    private String term;
    private Double score;
}