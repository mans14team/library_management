package com.example.library_management.domain.book.service;

import com.example.library_management.domain.book.dto.BookResponseDtos;
import com.example.library_management.domain.book.dto.SearchCriteria;
import com.example.library_management.domain.book.entity.Book;
import com.example.library_management.domain.book.entity.BookDocument;
import com.example.library_management.domain.book.exception.FindBookException;
import com.example.library_management.domain.book.repository.BookRepository;
import com.example.library_management.domain.book.repository.BookSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookSearchService {
    private final BookSearchRepository bookSearchRepository;
    private final BookRepository bookRepository;

    // Elasticsearch에 도서 정보를 인덱싱
    public void indexBook(Book book) {
        try {
            BookDocument document = new BookDocument(book);
            bookSearchRepository.save(document);
            log.info("Book indexed successfully: {}", book.getId());
        } catch (Exception e) {
            log.error("Error indexing book: {}", e.getMessage());
        }
    }

    // 도서를 다양한 조건으로 검색
    public Page<BookResponseDtos> search(SearchCriteria searchCriteria, Pageable pageable) {
        PageRequest pageRequest = createPageRequest(pageable, searchCriteria.getSortField());

        switch (searchCriteria.getSearchType()) {
            case ISBN:
                return searchByIsbn(searchCriteria.getIsbn(), pageable);
            case FUZZY:
                return searchByFuzzy(searchCriteria.getBookTitle(), pageable);
            case COMPREHENSIVE:
                return searchComprehensive(searchCriteria.getSearchTerm(), pageable);
            case SUBJECT:
                return searchBySubjects(searchCriteria.getSubjects(), pageable);
            default:
                return searchDefault(searchCriteria, pageable);
        }
    }

    // PageRequest 생성 메서드 추가
    private PageRequest createPageRequest(Pageable pageable, String sortField) {
        if (sortField != null && !sortField.isEmpty()) {
            return PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.ASC, sortField)
            );
        }
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
    }

    // ISBN으로 도서를 검색
    private Page<BookResponseDtos> searchByIsbn(String isbn, Pageable pageable) {
        return bookSearchRepository.findByIsbnContaining(isbn, pageable)
                .map(this::convertToDto);
    }

    // 퍼지 검색으로 도서를 검색 (오타 허용)
    private Page<BookResponseDtos> searchByFuzzy(String title, Pageable pageable) {
        return bookSearchRepository.findByTitleFuzzy(title, pageable)
                .map(this::convertToDto);
    }

    // 전체 필드에 대해 가중치를 적용하여 검색
    private Page<BookResponseDtos> searchComprehensive(String searchTerm, Pageable pageable) {
        return bookSearchRepository.searchAllFields(searchTerm, pageable)
                .map(this::convertToDto);
    }

    // 주제 목록으로 도서를 검색
    private Page<BookResponseDtos> searchBySubjects(List<String> subjects, Pageable pageable) {
        return bookSearchRepository.findBySubjectsIn(subjects, pageable)
                .map(this::convertToDto);
    }

    // 기본 검색 조건으로 도서를 검색
    private Page<BookResponseDtos> searchDefault(SearchCriteria criteria, Pageable pageable) {
        return bookSearchRepository.searchBooks(
                criteria.getBookTitle(),
                criteria.getAuthor(),
                criteria.getPublisher(),
                criteria.getSubjects() != null && !criteria.getSubjects().isEmpty()
                        ? criteria.getSubjects().get(0)
                        : null,
                pageable
        ).map(this::convertToDto);
    }

    // BookDocument를 BookResponseDtos로 변환
    private BookResponseDtos convertToDto(BookDocument document) {
        Book book = bookRepository.findById(Long.valueOf(document.getId()))
                .orElseThrow(FindBookException::new);
        return new BookResponseDtos(book);
    }

    // 도서 삭제 시 Elasticsearch에서도 삭제
    public void deleteBookDocument(String id) {
        try {
            bookSearchRepository.deleteById(id);
            log.info("Book document deleted successfully: {}", id);
        } catch (Exception e) {
            log.error("Error deleting book document: {}", e.getMessage());
        }
    }

    //  MySQL의 도서 데이터를 Elasticsearch와 동기화
    public void syncBooks() {
        log.info("Starting book index synchronization");
        List<Book> allBooks = bookRepository.findAll();
        allBooks.forEach(this::indexBook);
        log.info("Completed book index synchronization");
    }
}
