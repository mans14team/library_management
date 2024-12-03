package com.example.library_management.domain.book.controller;

import com.example.library_management.domain.book.dto.*;
import com.example.library_management.domain.book.enums.SearchType;
import com.example.library_management.domain.book.service.BookSearchService;
import com.example.library_management.domain.book.service.BookService;
import com.example.library_management.domain.book.service.SearchSuggestionService;
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
    private final BookSearchService bookSearchService;
    private final SearchSuggestionService searchSuggestionService;
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
     * @param searchType 검색 유형 (DEFAULT, FUZZY, COMPREHENSIVE, SUBJECT, ISBN)
     * @param searchTerm 통합 검색어 (전체 검색용)
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
     * Elasticsearch 동기화 API
     * MySQL의 도서 데이터를 Elasticsearch와 동기화합니다.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/sync")
    public ResponseEntity<ApiResponse<String>> syncBooks(){
        return ResponseEntity.ok(ApiResponse.success(bookSearchService.syncBooks()));
    }

    /**
     * 도서 제목과 저자 자동완성 API
     * @param prefix 검색할 접두어
     * @param size 반환할 제안 수
     * @return 자동완성 제안 목록
     */
    @GetMapping("/autocomplete")
    public ResponseEntity<ApiResponse<List<SuggestResponse>>> autoComplete(@RequestParam String prefix, @RequestParam(defaultValue = "5") int size){
        return ResponseEntity.ok(ApiResponse.success(bookSearchService.autoComplete(prefix, size)));
    }

    /**
     * 연관 검색어 추천 API
     * @param searchTerm 검색어
     * @return 추천된 연관 검색어 목록
     */
    @GetMapping("/search/related-terms")
    public ResponseEntity<ApiResponse<List<RelatedSearchResponse>>> getRelatedSearchTerms(@RequestParam String searchTerm){
        return ResponseEntity.ok(ApiResponse.success(searchSuggestionService.getRelatedSearchTerms(searchTerm)));
    }

    /**
     * 도서 선택 기록 API
     * 사용자가 검색 결과에서 도서를 선택했을 때 호출되어, 검색어와 선택된 도서 간의 관계를 기록합니다.
     * 이 데이터는 연관 검색어 추천 시스템에서 사용됩니다.
     *
     * @param searchTerm 사용자가 입력한 검색어
     * @param bookId 선택된 도서의 ID
     * @return 처리 결과
     */
    @PostMapping("/search/record-selection")
    public ResponseEntity<ApiResponse<Void>> recordBookSelection(
            @RequestParam String searchTerm,
            @RequestParam Long bookId
    ) {
        bookService.recordBookSelection(searchTerm, bookId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 인기 검색어 조회 API
     * 사용자들이 가장 많이 검색한 검색어를 검색 횟수 순으로 반환합니다.
     *
     * @param size 반환할 인기 검색어의 개수 (기본값: 10)
     * @return 인기 검색어 목록
     *         - 검색어(term)
     *         - 검색 점수(score): 검색 횟수 기반
     */
    @GetMapping("/search/popular-terms")
    public ResponseEntity<ApiResponse<List<RelatedSearchResponse>>> getPopularSearchTerms(
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                searchSuggestionService.getPopularSearchTerms(size)));
    }
}
