package com.example.library_management.domain.book.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.ArrayList;
import java.util.List;

@Document(indexName = "books")
@Getter
@NoArgsConstructor
public class BookDocument {
    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String isbn;

    @Field(type = FieldType.Text)
    private String bookTitle;

    @Field(type = FieldType.Long)
    private Long bookPublished;

    @Field(type = FieldType.Keyword)
    private List<String> authors;

    @Field(type = FieldType.Keyword)
    private List<String> publishers;

    @Field(type = FieldType.Keyword)
    private List<String> subjects;

    // Book 엔티티를 BookDocument로 변환하는 생성자
    public BookDocument(Book book) {
        this.id = book.getId().toString();
        this.isbn = book.getIsbn();
        this.bookTitle = book.getBookTitle();
        this.bookPublished = book.getBookPublished();
        this.authors = new ArrayList<>(book.getAuthors());
        this.publishers = new ArrayList<>(book.getPublishers());
        this.subjects = new ArrayList<>(book.getSubjects());
    }
}
