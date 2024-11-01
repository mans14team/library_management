package com.example.library_management.domain.book.cache.bookAuthor.entity;

import com.example.library_management.domain.book.entity.Book;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@RequiredArgsConstructor
public class BookAuthor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String authorName;

    @ManyToMany(mappedBy = "authors")
    private Set<Book> books = new HashSet<>();

    public BookAuthor(String authorName) {
        this.authorName = authorName;
    }
}
