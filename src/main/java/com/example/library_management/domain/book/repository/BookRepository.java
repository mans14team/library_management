package com.example.library_management.domain.book.repository;

import com.example.library_management.domain.book.entity.Book;
import com.example.library_management.domain.bookCategory.entity.BookCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findAllByCategory(BookCategory category);
}
