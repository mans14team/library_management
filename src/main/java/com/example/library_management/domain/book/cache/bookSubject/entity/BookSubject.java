package com.example.library_management.domain.book.cache.bookSubject.entity;

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
public class BookSubject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String subjectName;

    public BookSubject(String subjectName) {
        this.subjectName = subjectName;
    }
}
