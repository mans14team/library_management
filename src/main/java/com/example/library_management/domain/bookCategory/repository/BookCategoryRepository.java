package com.example.library_management.domain.bookCategory.repository;

import com.example.library_management.domain.bookCategory.entity.BookCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookCategoryRepository extends JpaRepository<BookCategory, Long> {
    BookCategory findByCategoryName(String category);
}
