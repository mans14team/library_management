package com.example.library_management.domain.book.repository;

import com.example.library_management.domain.book.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookRepositoryCustom {
    Page<Book> searchBooks(String isbn, String bookTitle, String author, String publisher, List<String> authors, Pageable pageable);
}
