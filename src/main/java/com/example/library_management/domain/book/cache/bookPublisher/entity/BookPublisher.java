package com.example.library_management.domain.book.cache.bookPublisher.entity;

import com.example.library_management.domain.book.entity.Book;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@RequiredArgsConstructor
public class BookPublisher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String publisherName;

    public BookPublisher(String publisherName) {
        this.publisherName = publisherName;
    }
}
