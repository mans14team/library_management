package com.example.library_management.domain.book.cache.bookPublisher.repository;

import com.example.library_management.domain.book.cache.bookPublisher.entity.BookPublisher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookPublisherRepository extends JpaRepository<BookPublisher, Long> {
    Optional<BookPublisher> findByPublisherName(String name);

    void deleteByPublisherName(String name);
}
