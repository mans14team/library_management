package com.example.library_management.domain.book.cache.bookSubject.repository;

import com.example.library_management.domain.book.cache.bookSubject.entity.BookSubject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookSubjectRepository extends JpaRepository<BookSubject,Long> {
    Optional<BookSubject> findBySubjectName(String name);

    void deleteBySubjectName(String name);
}
