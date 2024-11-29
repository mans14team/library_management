package com.example.library_management.domain.book.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.example.library_management.domain.book.dto.BookResponseDtos;
import com.example.library_management.domain.book.dto.SearchCriteria;
import com.example.library_management.domain.book.dto.SuggestResponse;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookSearchService {
    private final BookSearchRepository bookSearchRepository;
    private final BookRepository bookRepository;
    private final ElasticsearchClient elasticsearchClient;

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

    @Transactional(readOnly = true)
    public List<SuggestResponse> autoComplete(String prefix, int size) {
        if (prefix == null || prefix.length() < 2) {
            return Collections.emptyList();
        }

        try {
            log.info("[자동완성 검색 시작] 접두어: {}, 요청 크기: {}", prefix, size);

            // 검색 요청 생성
            SearchResponse<BookDocument> response = elasticsearchClient.search(s -> s
                            .index("books")
                            .suggest(suggest -> suggest
                                    // 제목 자동완성
                                    .suggesters("title_suggest", titleSuggester -> titleSuggester
                                            .prefix(prefix)
                                            .completion(c -> c
                                                    .field("titleSuggest")
                                                    .skipDuplicates(false)
                                                    .size(size * 5)
                                                    .fuzzy(f -> f
                                                            .fuzziness("AUTO")
                                                            .transpositions(true)
                                                            .minLength(2) // 최소 길이 2로 수정
                                                            .prefixLength(1)
                                                    )
                                            )
                                    )
                                    // 저자 자동완성
                                    .suggesters("author_suggest", authorSuggester -> authorSuggester
                                            .prefix(prefix)
                                            .completion(c -> c
                                                    .field("authorSuggest")
                                                    .skipDuplicates(false)
                                                    .size(size * 5)
                                                    .fuzzy(f -> f
                                                            .fuzziness("AUTO")
                                                            .transpositions(true)
                                                            .minLength(2) // 최소 길이 2로 수정
                                                            .prefixLength(1)
                                                    )
                                            )
                                    )
                            ),
                    BookDocument.class
            );

            List<SuggestResponse> suggestions = new ArrayList<>();

            // 제목 제안 처리
            var titleSuggestions = response.suggest().get("title_suggest");
            if (titleSuggestions != null && !titleSuggestions.isEmpty()) {
                // 모든 suggestion을 처리하도록 수정
                log.info("제목 suggestion 수: {}", titleSuggestions.size());
                for (var suggestion : titleSuggestions) {
                    suggestion.completion().options().forEach(option -> {
                        BookDocument source = option.source();
                        if (source != null) {
                            suggestions.add(new SuggestResponse(
                                    option.text(),
                                    "TITLE",
                                    option.score(),
                                    source.getBookTitle(),
                                    source.getAuthors()
                            ));
                        }
                    });
                }
            }

            // 저자 제안 처리
            var authorSuggestions = response.suggest().get("author_suggest");
            if (authorSuggestions != null && !authorSuggestions.isEmpty()) {
                // 모든 suggestion을 처리하도록 수정
                log.info("제목 suggestion 수: {}", authorSuggestions.size());
                for (var suggestion : authorSuggestions) {
                    suggestion.completion().options().forEach(option -> {
                        BookDocument source = option.source();
                        if (source != null) {
                            suggestions.add(new SuggestResponse(
                                    option.text(),
                                    "AUTHOR",
                                    option.score(),
                                    source.getBookTitle(),
                                    source.getAuthors()
                            ));
                        }
                    });
                }
            }

            // 결과 정렬 및 중복 제거
            var result = suggestions.stream()
                    .distinct()
                    .sorted((s1, s2) -> {
                        if (s1.getType().equals("TITLE") && s2.getType().equals("AUTHOR")) {
                            return -1; // TITLE을 우선 정렬
                        } else if (s1.getType().equals("AUTHOR") && s2.getType().equals("TITLE")) {
                            return 1;
                        }
                        return Double.compare(s2.getScore(), s1.getScore());
                    })
                    .limit(size)
                    .collect(Collectors.toList());

            log.info("[자동완성 검색 완료] 접두어: {}, 검색된 결과 수: {}", prefix, result.size());

            // 결과 정렬 및 중복 제거
            return result;
        } catch (Exception e) {
            log.error("[자동완성 검색 실패] 접두어: {}, 에러: {}", prefix, e.getMessage(), e);
            // 검색 실패 시 빈 결과 반환 (사용자 경험 향상)
            return Collections.emptyList();
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
