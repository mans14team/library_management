package com.example.library_management.domain.book.service;

import com.example.library_management.domain.book.dto.BookReponseDto;
import com.example.library_management.domain.book.dto.BookRequestDto;
import com.example.library_management.domain.book.entity.Book;
import com.example.library_management.domain.book.exception.FindCatogoryException;
import com.example.library_management.domain.book.repository.BookRepository;
import com.example.library_management.domain.bookCategory.repository.BookCategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class BookService {
    private final BookRepository bookRepository;
    private final BookCategoryRepository categoryRepository;
    public BookReponseDto addBook(BookRequestDto bookRequestDto) {
        Book book = new Book();
        book.setBookTitle(bookRequestDto.getBookTitle());
        book.setBookDescription(bookRequestDto.getBookDescription());
        book.setBookAuthor(bookRequestDto.getBookAuthor());
        book.setBookPublisher(bookRequestDto.getBookPublisher());
        book.setBookPublished(bookRequestDto.getBookPublished());
        book.setCategory(
                categoryRepository
                        .findById(bookRequestDto.getCategoryId())
                        .orElseThrow(
                                () -> new FindCatogoryException()
                        )
        );

        Book savedBook = bookRepository.save(book);

        return new BookReponseDto(savedBook);
    }
}
