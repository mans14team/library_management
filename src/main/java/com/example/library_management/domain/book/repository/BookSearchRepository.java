package com.example.library_management.domain.book.repository;

import com.example.library_management.domain.book.entity.BookDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookSearchRepository extends ElasticsearchRepository<BookDocument, String> {
    // 기본 ISBN 검색
    @Query("""
            {
              "match": {
                "isbn": {
                  "query": "?0",
                  "operator": "and"
                }
              }
            }
            """)
    Page<BookDocument> findByIsbnContaining(String isbn, Pageable pageable);

    // 제목 검색 (정렬 지원)
    @Query("""
            {
              "bool": {
                "must": [
                  {
                    "match": {
                      "bookTitle": {
                        "query": "?0",
                        "operator": "and"
                      }
                    }
                  }
                ],
                "should": [
                  {
                    "match_phrase": {
                      "bookTitle": {
                        "query": "?0",
                        "boost": 2.0
                      }
                    }
                  }
                ]
              }
            }
            """)
    Page<BookDocument> findByBookTitleContaining(String title, Pageable pageable);

    // 퍼지 검색 (오타 허용)
    @Query("""
            {
              "bool": {
                "should": [
                  {
                    "fuzzy": {
                      "bookTitle": {
                        "value": "?0",
                        "fuzziness": "AUTO",
                        "prefix_length": 2
                      }
                    }
                  },
                  {
                    "fuzzy": {
                      "bookTitleNgram": {
                        "value": "?0",
                        "fuzziness": "AUTO",
                        "prefix_length": 1
                      }
                    }
                  }
                ]
              }
            }
            """)
    Page<BookDocument> findByTitleFuzzy(String title, Pageable pageable);

    // 복합 검색 (제목, 저자, 출판사, 주제)
    @Query("""
            {
              "bool": {
                "should": [
                  {"match": {"bookTitle": {"query": "?0", "boost": 2.0}}},
                  {"match": {"authors": {"query": "?1", "boost": 1.5}}},
                  {"match": {"publishers": {"query": "?2"}}},
                  {"match": {"subjects": {"query": "?3"}}}
                ],
                "minimum_should_match": 1,
                "filter": [
                  {"bool": {"must": []}}
                ]
              }
            }
            """)
    Page<BookDocument> searchBooks(String title, String author, String publisher, String subject, Pageable pageable);

    // 정확한 주제 검색
    @Query("""
            {
              "bool": {
                "must": [
                  {
                    "terms": {
                      "subjects": "?0"
                    }
                  }
                ]
              }
            }
            """)
    Page<BookDocument> findBySubjectsIn(List<String> subjects, Pageable pageable);

    // 출판사별 도서 검색 (정렬 지원)
    @Query("""
            {
              "bool": {
                "must": [
                  {
                    "match": {
                      "publishers": {
                        "query": "?0",
                        "operator": "and"
                      }
                    }
                  }
                ]
              }
            }
            """)
    Page<BookDocument> findByPublishersContaining(String publisher, Pageable pageable);

    // 저자별 도서 검색 (정렬 지원)
    @Query("""
            {
              "bool": {
                "must": [
                  {
                    "match": {
                      "authors": {
                        "query": "?0",batch_job_execution
                        "operator": "and"
                      }
                    }
                  }
                ]
              }
            }
            """)
    Page<BookDocument> findByAuthorsContaining(String author, Pageable pageable);

    // 복합 조건 검색 (AND 조건)
    @Query("{\"bool\": {\"must\": [" +
            "{\"match\": {\"bookTitle\": \"?0\"}}," +
            "{\"match\": {\"authors\": \"?1\"}}" +
            "]}}")
    Page<BookDocument> findByTitleAndAuthor(String title, String author, Pageable pageable);

    // 검색어로 전체 텍스트 검색
    @Query("""
            {
              "bool": {
                "should": [
                  {
                    "multi_match": {
                      "query": "?0",
                      "fields": [
                        "bookTitle^3",
                        "authors^2",
                        "publishers",
                        "subjects"
                      ],
                      "type": "best_fields",
                      "operator": "and"
                    }
                  },
                  {
                    "term": {
                      "bookPublished": {
                        "value": "?0",
                        "boost": 2.0
                      }
                    }
                  }
                ],
                "minimum_should_match": 1
              }
            }
            """)
    Page<BookDocument> searchAllFields(String searchTerm, Pageable pageable);
}
