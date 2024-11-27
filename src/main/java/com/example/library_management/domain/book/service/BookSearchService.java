package com.example.library_management.domain.book.service;

import com.example.library_management.domain.book.dto.BookResponseDtos;
import com.example.library_management.domain.book.dto.SearchCriteria;
import com.example.library_management.domain.book.entity.Book;
import com.example.library_management.domain.book.entity.BookDocument;
import com.example.library_management.domain.book.exception.BookException;
import com.example.library_management.domain.book.exception.FindBookException;
import com.example.library_management.domain.book.repository.BookRepository;
import com.example.library_management.domain.book.repository.BookSearchRepository;
import com.example.library_management.domain.common.exception.GlobalExceptionConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            log.info("[Elasticsearch 인덱싱 시작] 도서 ID: {}, 제목: {}", book.getId(), book.getBookTitle());

            BookDocument document = new BookDocument(book);
            bookSearchRepository.save(document);
            log.info("[Elasticsearch 인덱싱 완료] 도서 ID: {}, 제목: {}", book.getId(), book.getBookTitle());
        } catch (Exception e) {
            log.error("[Elasticsearch 인덱싱 실패] 도서 ID: {}, 에러: {}", book.getId(), e.getMessage());
            throw new BookException(GlobalExceptionConst.ELASTICSEARCH_INDEX_ERROR);
        }
    }

    // 도서를 다양한 조건으로 검색
    @Transactional(readOnly = true)
    public Page<BookResponseDtos> search(SearchCriteria searchCriteria, Pageable pageable) {
        PageRequest pageRequest = createPageRequest(pageable, searchCriteria.getSortField());
        log.info("[검색 시작] 검색 유형: {}", searchCriteria.getSearchType());

        try {
            Page<BookResponseDtos> results = switch (searchCriteria.getSearchType()) {
                case ISBN -> searchByIsbn(searchCriteria.getIsbn(), pageable);
                case FUZZY -> searchByFuzzy(searchCriteria.getBookTitle(), pageable);
                case COMPREHENSIVE -> searchComprehensive(searchCriteria.getSearchTerm(), pageable);
                case SUBJECT -> searchBySubjects(searchCriteria.getSubjects(), pageable);
                case DEFAULT -> searchDefault(searchCriteria, pageable);
            };

            log.info("[검색 완료] 검색 결과 수: {}", results.getTotalElements());
            return results;
        } catch (Exception e) {                               // 예외 처리 추가
            log.error("[검색 실패] 에러: {}", e.getMessage());
            throw new BookException(GlobalExceptionConst.ELASTICSEARCH_SEARCH_ERROR);
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
        if (title == null || title.isEmpty()) {              // null 체크 추가
            throw new BookException(GlobalExceptionConst.ELASTICSEARCH_INVALID_QUERY);
        }

        return bookSearchRepository.findByTitleFuzzy(title, pageable)
                .map(this::convertToDto);
    }

    // 전체 필드에 대해 가중치를 적용하여 검색
    private Page<BookResponseDtos> searchComprehensive(String searchTerm, Pageable pageable) {
        if (searchTerm == null || searchTerm.isEmpty()) {     // null 체크 추가
            throw new BookException(GlobalExceptionConst.ELASTICSEARCH_INVALID_QUERY);
        }

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
        String title = criteria.getBookTitle() != null ? criteria.getBookTitle() : "";
        String author = criteria.getAuthor() != null ? criteria.getAuthor() : "";
        String publisher = criteria.getPublisher() != null ? criteria.getPublisher() : "";
        String subject = (criteria.getSubjects() != null && !criteria.getSubjects().isEmpty())
                ? criteria.getSubjects().get(0)
                : "";

        return bookSearchRepository.searchBooks(
                title,
                author,
                publisher,
                subject,
                pageable
        ).map(this::convertToDto);
    }

    // BookDocument를 BookResponseDtos로 변환
    private BookResponseDtos convertToDto(BookDocument document) {
        try {
            Book book = bookRepository.findById(Long.valueOf(document.getId()))
                    .orElseThrow(() -> new FindBookException());
            return new BookResponseDtos(book);
        } catch (NumberFormatException e) {
            throw new BookException(GlobalExceptionConst.ELASTICSEARCH_SEARCH_ERROR);
        }
    }

    // 도서 삭제 시 Elasticsearch에서도 삭제
    public void deleteBookDocument(String id) {
        try {
            bookSearchRepository.deleteById(id);
            log.info("[Elasticsearch 도서 삭제 완료] ID: {}", id);
        } catch (Exception e) {
            log.error("[Elasticsearch 도서 삭제 실패] ID: {}, 에러: {}", id, e.getMessage());
            throw new BookException(GlobalExceptionConst.ELASTICSEARCH_DELETE_ERROR);
        }
    }

    //  MySQL의 도서 데이터를 Elasticsearch와 동기화
    @Transactional(readOnly = true)
    public String syncBooks() {
        log.info("[Elasticsearch 전체 동기화 시작]");
        try {
            // 기존 인덱스 삭제
            bookSearchRepository.deleteAll();
            log.info("기존 인덱스 삭제 완료");

            List<Book> allBooks = bookRepository.findAll();
            log.info("동기화할 도서 수: {}", allBooks.size());

            // 동기화 진행상황 추적
            int totalBooks = allBooks.size();
            int successCount = 0;
            int failCount = 0;

            // 도서 동기화
            for (Book book : allBooks) {
                try {
                    indexBook(book);
                    successCount++;

                    if (successCount % 100 == 0) {  // 100건마다 진행상황 로깅
                        log.info("동기화 진행중: {}/{} 완료", successCount, totalBooks);
                    }
                } catch (Exception e) {
                    failCount++;
                    log.error("도서 인덱싱 실패 - ID: {}, 제목: {}, 에러: {}",
                            book.getId(), book.getBookTitle(), e.getMessage());
                }
            }

            log.info("[Elasticsearch 전체 동기화 완료] 성공: {}, 실패: {}",
                    successCount, failCount);

            log.info("[Elasticsearch 전체 동기화 완료]");

            return "도서 데이터 동기화가 완료되었습니다.";
        } catch (Exception e) {
            log.error("[Elasticsearch 전체 동기화 실패] 에러: {}", e.getMessage());
            throw new BookException(GlobalExceptionConst.ELASTICSEARCH_SYNC_ERROR);
        }
    }
}
