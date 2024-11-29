package com.example.library_management.domain.book.service;

import com.example.library_management.domain.book.controller.BookController;
import com.example.library_management.domain.book.dto.*;
import com.example.library_management.domain.book.entity.Book;
import com.example.library_management.domain.book.dto.SearchParams;
import com.example.library_management.domain.book.enums.SearchType;
import com.example.library_management.domain.book.exception.AuthorizedAdminException;
import com.example.library_management.domain.book.exception.FindBookException;
import com.example.library_management.domain.book.repository.BookRepository;
import com.example.library_management.domain.user.enums.UserRole;
import com.example.library_management.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookService {
    private final BookRepository bookRepository;
    private final BookSearchService bookSearchService;

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    public Boolean validateUser(UserDetailsImpl userDetails) {
        return userDetails.getUser().getRole().equals(UserRole.ROLE_ADMIN);
    }

    public BookResponseDto addBook(BookRequestDto bookRequestDto, UserDetailsImpl userDetails) {
        if(!validateUser(userDetails)){
            throw new AuthorizedAdminException();
        }

        Book book = new Book(
                bookRequestDto.getIsbn(),
                bookRequestDto.getBookTitle(),
                bookRequestDto.getBookPublished(),
                bookRequestDto.getAuthors(),
                bookRequestDto.getPublishers(),
                bookRequestDto.getSubjects()
        );

        Book savedBook = bookRepository.save(book);

        // Elasticsearch에 인덱싱
        bookSearchService.indexBook(savedBook);

        return new BookResponseDto(savedBook);
    }

    public BookResponseDto updateBook(Long bookId, BookUpdateRequestDto bookRequestDto, UserDetailsImpl userDetails) {
        if(!validateUser(userDetails)){
            throw new AuthorizedAdminException();
        }

        Book book = bookRepository.findById(bookId).orElseThrow(
                FindBookException::new
        );

        book.update(
                bookRequestDto.getBookTitle(),
                bookRequestDto.getBookPublished(),
                bookRequestDto.getAddAuthors(),
                bookRequestDto.getAddPublishers(),
                bookRequestDto.getAddSubjects(),
                bookRequestDto.getRemoveAuthors(),
                bookRequestDto.getRemovePublishers(),
                bookRequestDto.getRemoveSubjects()
        );

        Book savedBook = bookRepository.save(book);

        // Elasticsearch 인덱스 업데이트
        bookSearchService.indexBook(savedBook);

        return new BookResponseDto(savedBook);
    }

    public Long deleteBook(Long bookId, UserDetailsImpl userDetails) {
        if(!validateUser(userDetails)){
            throw new AuthorizedAdminException();
        }

        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new FindBookException()
        );

        bookRepository.delete(book);

        // BookSearchService를 통해 Elasticsearch 문서 삭제
        bookSearchService.deleteBookDocument(bookId.toString());

        return bookId;
    }

    public List<BookResponseDtos> getBooks() {
        List<Book> books = bookRepository.findAll();

        return books.stream().map(BookResponseDtos::new).toList();
    }

    public BookResponseDto getBookById(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(
                FindBookException::new
        );
        return new BookResponseDto(book);
    }

    @Transactional(readOnly = true)
    public Page<BookResponseDtos> searchBooks(SearchType searchType, SearchParams searchParams, Pageable pageable) {
        SearchCriteria criteria = SearchCriteria.builder()
                .searchType(searchType)
                .isbn(searchParams.getIsbn())
                .bookTitle(searchParams.getBookTitle())
                .author(searchParams.getAuthor())
                .publisher(searchParams.getPublisher())
                .subjects(searchParams.getSubjects())
                .searchTerm(searchParams.getSearchTerm())
                .sortField(searchParams.getSortField())
                .build();

        // Elasticsearch를 통한 검색
        return bookSearchService.search(criteria, pageable);
    }
}
