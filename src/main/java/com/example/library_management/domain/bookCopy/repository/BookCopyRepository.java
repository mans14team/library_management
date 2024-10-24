package com.example.library_management.domain.bookCopy.repository;

import com.example.library_management.domain.bookCopy.entity.BookCopy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {
}
