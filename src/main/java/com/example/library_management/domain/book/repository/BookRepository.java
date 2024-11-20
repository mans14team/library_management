package com.example.library_management.domain.book.repository;

import com.example.library_management.domain.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long>, BookRepositoryCustom {
    @Query("SELECT b.isbn FROM Book b")
    List<String> findAllIsbns();
}
