package com.example.library_management.domain.bookCopy.repository;

import com.example.library_management.domain.book.entity.Book;
import com.example.library_management.domain.bookCopy.entity.BookCopy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {
    List<BookCopy> findAllByBookAndRentableTrue(Book book);
}
