package com.example.library_management.domain.book.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.example.library_management.domain.book.dto.RelatedSearchResponse;
import com.example.library_management.domain.book.entity.BookDocument;
import com.example.library_management.domain.book.entity.SearchSuggestionDocument;
import com.example.library_management.domain.book.repository.SearchSuggestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchSuggestionService {
    private final SearchSuggestionRepository searchSuggestionRepository;
    private final ElasticsearchClient elasticsearchClient;

    // 추천 시스템 각 요소별 가충지 설정
    private static final double CONTENT_SIMILARITY_WEIGHT = 0.6;  // 콘텐츠 기반 추천 가중치
    private static final double SEARCH_FREQUENCY_WEIGHT = 0.2; // 검색 기반 추천 가중치
    private static final double CLICK_FREQUENCY_WEIGHT = 0.2; // 클릭 기반 추천 가중치

    // 검색어 기록 저장 및 업데이트
    @Transactional
    public void recordSearchTerm(String searchTerm){
        // 기존 검색어 기록 조회
        SearchSuggestionDocument suggestion = searchSuggestionRepository.findBySearchTerm(searchTerm);

        if (suggestion == null){
            suggestion = new SearchSuggestionDocument(searchTerm);   // 검색어 기록이 없다면 새롭게 생성
        }else {
            suggestion.incrementSearchCount();  // 검색어 기록이 있으면 클릭 횟수 증가
        }

        searchSuggestionRepository.save(suggestion);
    }

    // 도서 선택 기록 저장, 검색어와 선택된 도서 간의 관계를 분석하여 추천 품질 향상
    @Transactional
    public void recordBookSelection(String searchTerm, String bookId){
        SearchSuggestionDocument suggestion = searchSuggestionRepository.findBySearchTerm(searchTerm);
        if (suggestion != null){
            suggestion.incrementClickCount();   // 클릭 횟수 증가
            suggestion.updateRelatedBook(bookId, 1.0);  // 연관 도서 정보 업데이트
            searchSuggestionRepository.save(suggestion);
        }
    }

    // 인기 검색어 상위 N개 조회
    public List<RelatedSearchResponse> getPopularSearchTerms(int limit) {
        try {
            List<SearchSuggestionDocument> popularTerms =
                    searchSuggestionRepository.findTopByOrderBySearchCountDesc(limit);

            return popularTerms.stream()
                    .map(doc -> new RelatedSearchResponse(
                            doc.getSearchTerm(),
                            (double) doc.getSearchCount()
                    ))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("인기 검색어 조회 중 오류 발생: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    // 연관 검색어 추천
    public List<RelatedSearchResponse> getRelatedSearchTerms(String searchTerm){
        try {
            // 1. 도서 내용, 저자, 주제 등을 기반으로 한 콘텐츠 유사도 계산
            Map<String, Double> contentBasedScores = getContentBasedSuggestions(searchTerm);

            // 2. 사용자들의 검색 패턴과 클릭 행동을 기반으로 한 점수 계산
            Map<String, Double> behaviorBasedScores = getBehaviorBasedScores(searchTerm);

            // 3. 두 점수를 결합하여 최종 추천 목록 생성
            return combineAndSortScores(contentBasedScores, behaviorBasedScores);
        }catch (Exception e){
            log.error("연관 검색어 추천 중 오류 발생: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    // 콘텐츠 기반 유사도 계산 (More Like This 쿼리 사용)
    private Map<String, Double> getContentBasedSuggestions(String searchTerm) throws Exception {
        // More Like This 쿼리 실행
        SearchResponse<BookDocument> response = elasticsearchClient.search(s -> s
                .index("books")
                .query(q -> q
                        .moreLikeThis(m -> m
                                .fields("bookTitle", "authors", "subjects") // 비교할 필드들
                                .like(l -> l.text(searchTerm))    // 기준 검색어
                                .minTermFreq(1)     // 최소 용어 빈도
                                .maxQueryTerms(12)    // 최대 쿼리 용어 수
                        )
                ), BookDocument.class);

        // 검색 결과를 점수화하여 Map에 저장
        Map<String, Double> contentScores = new HashMap<>();
        response.hits().hits().forEach(hit -> {
            BookDocument doc = hit.source();
            if (doc != null) {
                // 도서 제목에 가장 높은 가중치 부여
                contentScores.put(doc.getBookTitle(), hit.score());
                // 저자와 주제도 연관 검색어로 추가
                doc.getAuthors().forEach(author ->
                        contentScores.put(author, hit.score() * 0.8));   // 저자는 제목의 80% 가중치로 추가
                doc.getSubjects().forEach(subject ->
                        contentScores.put(subject, hit.score() * 0.6));  // 주제는 제목의 60% 가중치로 추가
            }
        });

        return contentScores;
    }

    // 사용자 행동 기반 점수 계산
    private Map<String, Double> getBehaviorBasedScores(String searchTerm) {
        Map<String, Double> behaviorScores = new HashMap<>();

        // 상위 10개의 연관 검색어 조회
        List<SearchSuggestionDocument> relatedDocs =
                searchSuggestionRepository.findRelatedSearchTerms(searchTerm, 10);

        // 각 연관 검색어에 대해 검색 빈도와 클릭 빈도를 결합한 점수 계산
        for (SearchSuggestionDocument doc : relatedDocs) {
            double score = (doc.getSearchCount() * SEARCH_FREQUENCY_WEIGHT) +  // 검색 빈도 점수
                    (doc.getClickCount() * CLICK_FREQUENCY_WEIGHT);   // 클릭 빈도 점수
            behaviorScores.put(doc.getSearchTerm(), score);
        }

        return behaviorScores;
    }

    // 최종 점수 계산 및 정렬
    private List<RelatedSearchResponse> combineAndSortScores(Map<String, Double> contentScores, Map<String, Double> behaviorScores) {
        Map<String, Double> finalScores = new HashMap<>();

        // 콘텐츠 기반 점수에 가중치 적용하여 합산
        contentScores.forEach((term, score) ->
                finalScores.merge(term, score * CONTENT_SIMILARITY_WEIGHT, Double::sum));

        // 사용자 행동 기반 점수에 가중치 적용하여 합산
        behaviorScores.forEach((term, score) ->
                finalScores.merge(term, score * (SEARCH_FREQUENCY_WEIGHT + CLICK_FREQUENCY_WEIGHT), Double::sum));

        // 최종 점수를 기준으로 정렬하여 상위 10개 반환
        return finalScores.entrySet().stream()
                .map(entry -> new RelatedSearchResponse(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(RelatedSearchResponse::getScore).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }
}