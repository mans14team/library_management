package com.example.library_management.domain.book.controller;

import com.example.library_management.domain.book.dto.BookResponseDto;
import com.example.library_management.domain.book.dto.BookRequestDto;
import com.example.library_management.domain.book.dto.BookResponseDtos;
import com.example.library_management.domain.book.dto.BookUpdateRequestDto;
import com.example.library_management.domain.book.dto.SearchParams;
import com.example.library_management.domain.book.enums.SearchType;
import com.example.library_management.domain.book.service.BookService;
import com.example.library_management.global.config.ApiResponse;
import com.example.library_management.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@RequestMapping("/library/books")
@Slf4j
public class BookController {
    private final BookService bookService;
    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<BookResponseDto> addBook(@RequestBody BookRequestDto bookRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        BookResponseDto bookResponseDto = bookService.addBook(bookRequestDto, userDetails);
        logger.info(userDetails.getUsername());
        return ResponseEntity.ok(bookResponseDto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{book_id}")
    public ResponseEntity<BookResponseDto> updateBook(@PathVariable("book_id") Long bookId, @AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody BookUpdateRequestDto bookRequestDto) {
        BookResponseDto bookResponseDto = bookService.updateBook(bookId, bookRequestDto, userDetails);
        return ResponseEntity.ok(bookResponseDto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
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


    /**
     * 도서 검색 API
     * @param searchType 검색 유형 (선택적)
     * @param searchTerm 통합 검색어 (선택적)
     * @param isbn ISBN (선택적)
     * @param bookTitle 도서 제목 (선택적)
     * @param author 저자 (선택적)
     * @param publisher 출판사 (선택적)
     * @param subjects 주제 목록 (선택적)
     * @param pageable 페이징 정보
     * @return 검색된 도서 목록
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<BookResponseDtos>>> searchBooks(
            @RequestParam(required = false, defaultValue = "DEFAULT") SearchType searchType,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) String bookTitle,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String publisher,
            @RequestParam(required = false) List<String> subjects,
            Pageable pageable
    ) {
        SearchParams searchParams = SearchParams.builder()
                .isbn(isbn)
                .bookTitle(bookTitle)
                .author(author)
                .publisher(publisher)
                .subjects(subjects)
                .searchTerm(searchTerm)
                .build();

        return ResponseEntity.ok(ApiResponse.success(bookService.searchBooks(searchType, searchParams, pageable)));
    }

    /**
     * 퍼지 검색 API (오타를 허용하는 검색)
     */
    @GetMapping("/search/fuzzy")
    public ResponseEntity<ApiResponse<Page<BookResponseDtos>>> fuzzySearch(
            @RequestParam String title,
            Pageable pageable
    ) {
        SearchParams searchParams = SearchParams.builder()
                .bookTitle(title)
                .build();

        return ResponseEntity.ok(ApiResponse.success(bookService.searchBooks(SearchType.FUZZY, searchParams, pageable)));
    }

    /**
     * 통합 검색 API (모든 필드를 대상으로 검색)
     */
    @GetMapping("/search/all")
    public ResponseEntity<ApiResponse<Page<BookResponseDtos>>> searchAllFields(
            @RequestParam String searchTerm,
            Pageable pageable
    ) {
        SearchParams searchParams = SearchParams.builder()
                .searchTerm(searchTerm)
                .build();

        return ResponseEntity.ok(ApiResponse.success(bookService.searchBooks(SearchType.COMPREHENSIVE, searchParams, pageable)));
    }

    /**
     * 주제별 검색 API
     */
    @GetMapping("/search/subjects")
    public ResponseEntity<ApiResponse<Page<BookResponseDtos>>> searchBySubjects(
            @RequestParam List<String> subjects,
            Pageable pageable
    ) {
        SearchParams searchParams = SearchParams.builder()
                .subjects(subjects)
                .build();

        return ResponseEntity.ok(ApiResponse.success(bookService.searchBooks(SearchType.SUBJECT, searchParams, pageable)));
    }
}
