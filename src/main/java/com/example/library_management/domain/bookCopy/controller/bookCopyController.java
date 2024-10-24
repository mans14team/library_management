package com.example.library_management.domain.bookCopy.controller;

import com.example.library_management.domain.bookCopy.dto.BookCopyRequestDto;
import com.example.library_management.domain.bookCopy.dto.BookCopyResponseDto;
import com.example.library_management.domain.bookCopy.service.BookCopyService;
import com.example.library_management.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/books/copies")
public class bookCopyController {
    private final BookCopyService bookCopyService;

    @PostMapping
    public BookCopyResponseDto addBookCopy(@RequestBody BookCopyRequestDto bookCopyRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return null;
    }
}
