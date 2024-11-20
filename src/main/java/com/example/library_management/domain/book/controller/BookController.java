package com.example.library_management.domain.book.controller;

import com.example.library_management.domain.book.dto.BookResponseDto;
import com.example.library_management.domain.book.dto.BookRequestDto;
import com.example.library_management.domain.book.dto.BookResponseDtos;
import com.example.library_management.domain.book.dto.BookUpdateRequestDto;
import com.example.library_management.domain.book.service.BookService;
import com.example.library_management.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<BookResponseDto> updateBook(@PathVariable("book_id") Long bookId, @AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody BookUpdateRequestDto bookRequestDto) {
        BookResponseDto bookResponseDto = bookService.updateBook(bookId, bookRequestDto, userDetails);
        return ResponseEntity.ok(bookResponseDto);
    }

    @DeleteMapping("/{book_id}")
    public ResponseEntity<Long> deleteBook(@PathVariable("book_id") Long bookId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long deletedId = bookService.deleteBook(bookId, userDetails);
        return ResponseEntity.ok(deletedId);
    }

//    @GetMapping
//    public ResponseEntity<List<BookResponseDtos>> getBooks(@RequestParam(value = "category", required = false) Long categoryId) {
//        List<BookResponseDtos> bookResponseDtosList;
//
//        bookResponseDtosList = bookService.getBooks();
//
//        return ResponseEntity.ok(bookResponseDtosList);
//    }

    @GetMapping("/{book_id}")
    public ResponseEntity<BookResponseDto> getBookById(@PathVariable("book_id") Long bookId) {
        BookResponseDto bookResponseDto = bookService.getBookById(bookId);
        return ResponseEntity.ok(bookResponseDto);
    }


    //검색 - 리퀘스트 파람(required=false)로 저자, 제목, 태그 등등 입력 받아서 서비스 호출
    // -> 서비스에선 커스텀으로 정의한 쿼리 불러서 처리.
    // 페이징은 어떻게 할지 고민하기
    @GetMapping
    public ResponseEntity<Page<BookResponseDtos>> searchBooks(
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) String bookTitle,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String publisher,
            @RequestParam(required = false) List<String> subjects,
            Pageable pageable
    ) {
        Page<BookResponseDtos> pagingBooks = bookService.searchBooks(isbn, bookTitle, author, publisher, subjects, pageable);

        return ResponseEntity.ok(pagingBooks);
    }
}
