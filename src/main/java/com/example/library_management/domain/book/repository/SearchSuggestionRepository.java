package com.example.library_management.domain.book.repository;

import com.example.library_management.domain.book.entity.SearchSuggestionDocument;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchSuggestionRepository extends ElasticsearchRepository<SearchSuggestionDocument, String> {
    // 검색어로 정확히 일치하는 문서 찾기
    SearchSuggestionDocument findBySearchTerm(String searchTerm);

    // 인기 검색어 상위 N개 조회
    @Query("""
            {
              "bool": {
                "must": [
                  {
                    "range": {
                      "searchCount": {
                        "gt": 0
                      }
                    }
                  }
                ]
              }
            }
            """)
    List<SearchSuggestionDocument> findTopByOrderBySearchCountDesc(int limit);

    // 특정 검색어와 연관된 검색어 조회
    @Query("""
            {
              "bool": {
                "should": [
                  {
                    "match": {
                      "bookTitle": {
                        "query": "?0",
                        "boost": 3.0,
                        "minimum_should_match": "2<70%"
                      }
                    }
                  },
                  {
                    "match": {
                      "subjects": {
                        "query": "?0",
                        "boost": 2.0
                      }
                    }
                  },
                  {
                    "match": {
                      "relatedTerms": {
                        "query": "?0",
                        "boost": 2.0
                      }
                    }
                  }
                ],
                "must_not": [
                  {
                    "match": {
                      "authors": {
                        "query": "?0"
                      }
                    }
                  }
                ],
                "minimum_should_match": 1,
                "boost_mode": "multiply"
              }
            }
            """)
    List<SearchSuggestionDocument> findRelatedSearchTerms(String searchTerm, int limit);

    // 특정 검색어와 연관된 주제어 조회
    @Query("""
            {
              "bool": {
                "should": [
                  {
                    "match": {
                      "subjects": {
                        "query": "?0",
                        "boost": 2.0
                      }
                    }
                  }
                ],
                "minimum_should_match": 1
              }
            }
            """)
    List<SearchSuggestionDocument> findRelatedSubjects(String searchTerm, int limit);
}