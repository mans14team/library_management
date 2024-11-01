package com.example.library_management.domain.book.cache.bookAuthor.repository;

import com.example.library_management.domain.book.cache.bookAuthor.entity.BookAuthor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookAuthorRepository extends JpaRepository<BookAuthor, Long> {
    Optional<BookAuthor> findByAuthorName(String name);

    void deleteByAuthorName(String name);
}
