package com.example.library_management.domain.book.controller;

import com.example.library_management.domain.book.dto.BookReponseDto;
import com.example.library_management.domain.book.entity.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/library/book")
public class BookController {
    @PostMapping
    public ResponseEntity<BookReponseDto> addBook(@RequestBody Book book) {

    }
}
