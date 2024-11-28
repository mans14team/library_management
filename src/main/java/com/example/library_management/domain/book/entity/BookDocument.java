package com.example.library_management.domain.book.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.CompletionField;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.core.suggest.Completion;

import java.util.*;

@Document(indexName = "books")
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookDocument {
    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String isbn;

    // 한글 검색을 위한 필드 설정
    @Field(type = FieldType.Text, analyzer = "korean_analyzer")
    private String bookTitle;

    // ngram 검색을 위한 필드
    @Field(type = FieldType.Text, analyzer = "edge_ngram_analyzer", searchAnalyzer = "korean_analyzer")
    private String bookTitleNgram;

    @Field(type = FieldType.Long)
    private Long bookPublished;

    // 저자 검색을 위한 필드 검색
    @Field(type = FieldType.Keyword, analyzer = "korean_analyzer")
    private List<String> authors;

    @Field(type = FieldType.Keyword)
    private List<String> publishers;

    @Field(type = FieldType.Keyword)
    private List<String> subjects;

    @CompletionField(maxInputLength = 100)
    private Completion titleSuggest; // 제목 자동완성용

    @CompletionField(maxInputLength = 100)
    private Completion authorSuggest; // 저자 자동완성용

    // Book 엔티티를 BookDocument로 변환하는 생성자
    public BookDocument(Book book) {
        this.id = book.getId().toString();
        this.isbn = book.getIsbn();
        this.bookTitle = book.getBookTitle();
        this.bookTitleNgram = book.getBookTitle();
        this.bookPublished = book.getBookPublished();
        this.authors = new ArrayList<>(book.getAuthors());
        this.publishers = new ArrayList<>(book.getPublishers());
        this.subjects = new ArrayList<>(book.getSubjects());

        // 제목 자동완성을 위한 입력값 생성
        List<String> titleInputs = new ArrayList<>();
        titleInputs.add(book.getBookTitle()); // 전체 제목

        // 공백으로 분리된 각 단어와 초성 추가
        String[] words = book.getBookTitle().split("\\s+");
        for (String word : words) {
            titleInputs.add(word);

            // 2글자 이상인 경우 모든 가능한 접두어 추가
            if (word.length() > 1) {
                for (int i = 1; i <= word.length(); i++) {
                    titleInputs.add(word.substring(0, i));
                }
            }
        }
        this.titleSuggest = new Completion(new ArrayList<>(new LinkedHashSet<>(titleInputs)));

        // 저자 자동완성을 위한 입력값 생성
        List<String> authorInputs = new ArrayList<>();
        for (String author : book.getAuthors()) {
            authorInputs.add(author);
            String[] authorWords = author.split("\\s+");
            for (String word : authorWords) {
                authorInputs.add(word);
                // 2글자 이상인 경우 모든 가능한 접두어 추가
                if (word.length() > 1) {
                    for (int i = 1; i <= word.length(); i++) {
                        authorInputs.add(word.substring(0, i));
                    }
                }
            }
        }
        this.authorSuggest = new Completion(authorInputs);
    }
}
