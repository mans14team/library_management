package com.example.library_management.domain.book.controller;

import com.example.library_management.domain.book.dto.BookReponseDto;
import com.example.library_management.domain.book.dto.BookRequestDto;
import com.example.library_management.domain.book.entity.Book;
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
@RequiredArgsConstructor
@RequestMapping("/library/book")
@Slf4j
public class BookController {
    private final BookService bookService;
    private static final Logger logger = LoggerFactory.getLogger(BookController.class);


    @PostMapping
    public ResponseEntity<BookReponseDto> addBook(@RequestBody BookRequestDto bookRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        BookReponseDto bookReponseDto = bookService.addBook(bookRequestDto);
        logger.info(userDetails.getUsername());
        return ResponseEntity.ok(bookReponseDto);
    }

    @PatchMapping("/{book_id}")
    public ResponseEntity<BookReponseDto> updateBook(@PathVariable("book_id") Long bookId, @AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody BookRequestDto bookRequestDto) {
        BookReponseDto bookReponseDto = bookService.updateBook(bookId, bookRequestDto, userDetails);
        return ResponseEntity.ok(bookReponseDto);
    }

    @DeleteMapping("/{book_id}")
    public ResponseEntity<Long> deleteBook(@PathVariable("book_id") Long bookId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long deletedId = bookService.deleteBook(bookId, userDetails);
        return ResponseEntity.ok(deletedId);
    }

    @GetMapping
    public ResponseEntity<List<BookReponseDto>> getBooks() {
        List<BookReponseDto> bookReponseDtoList = bookService.getBooks();
        return ResponseEntity.ok(bookReponseDtoList);
    }

    @GetMapping("/{book_id}")
    public ResponseEntity<BookReponseDto> getBookById(@PathVariable("book_id") Long bookId) {
        BookReponseDto bookReponseDto = bookService.getBookById(bookId);
        return ResponseEntity.ok(bookReponseDto);
    }

    @GetMapping()
    public ResponseEntity<List<BookReponseDto>> getBooksByCategory(@RequestParam("category") String category) {
        List<BookReponseDto> bookReponseDtoList = bookService.getBooksByCategory(category);
        return ResponseEntity.ok(bookReponseDtoList);
    }
}
