package com.example.library_management.domain.book.controller;

import com.example.library_management.domain.book.dto.BookResponseDto;
import com.example.library_management.domain.book.dto.BookRequestDto;
import com.example.library_management.domain.book.dto.BookResponseDtos;
import com.example.library_management.domain.book.service.BookService;
import com.example.library_management.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@RequestMapping("/api/books")
@Slf4j
public class BookController {
    private final BookService bookService;
    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    @PostMapping
    public ResponseEntity<BookResponseDto> addBook(@RequestBody BookRequestDto bookRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        BookResponseDto bookResponseDto = bookService.addBook(bookRequestDto, userDetails);
        logger.info(userDetails.getUsername());
        return ResponseEntity.ok(bookResponseDto);
    }

    @PutMapping("/{book_id}")
    public ResponseEntity<BookResponseDto> updateBook(@PathVariable("book_id") Long bookId, @AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody BookRequestDto bookRequestDto) {
        BookResponseDto bookResponseDto = bookService.updateBook(bookId, bookRequestDto, userDetails);
        return ResponseEntity.ok(bookResponseDto);
    }

    @DeleteMapping("/{book_id}")
    public ResponseEntity<Long> deleteBook(@PathVariable("book_id") Long bookId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long deletedId = bookService.deleteBook(bookId, userDetails);
        return ResponseEntity.ok(deletedId);
    }

    @GetMapping
    public ResponseEntity<List<BookResponseDtos>> getBooks(@RequestParam(value = "category", required = false) Long categoryId) {
        List<BookResponseDtos> bookResponseDtosList;

        bookResponseDtosList = bookService.getBooks();

        return ResponseEntity.ok(bookResponseDtosList);
    }

    @GetMapping("/{book_id}")
    public ResponseEntity<BookResponseDto> getBookById(@PathVariable("book_id") Long bookId) {
        BookResponseDto bookResponseDto = bookService.getBookById(bookId);
        return ResponseEntity.ok(bookResponseDto);
    }
}
