package com.example.library_management.domain.book.service;

import com.example.library_management.domain.book.controller.BookController;
import com.example.library_management.domain.book.dto.BookResponseDto;
import com.example.library_management.domain.book.dto.BookRequestDto;
import com.example.library_management.domain.book.dto.BookResponseDtos;
import com.example.library_management.domain.book.entity.Book;
import com.example.library_management.domain.book.exception.AuthorizedAdminException;
import com.example.library_management.domain.book.exception.FindBookException;
import com.example.library_management.domain.book.exception.FindCatogoryException;
import com.example.library_management.domain.book.repository.BookRepository;
import com.example.library_management.domain.user.enums.UserRole;
import com.example.library_management.global.security.UserDetailsImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookService {
    private final BookRepository bookRepository;
    private static final Logger logger = LoggerFactory.getLogger(BookController.class);


    public BookResponseDto addBook(BookRequestDto bookRequestDto, UserDetailsImpl userDetails) {
        if(!validateUser(userDetails)){
            throw new AuthorizedAdminException();
        }

        Book book = new Book(
                bookRequestDto.getIsbn(),
                bookRequestDto.getBookTitle(),
                bookRequestDto.getBookPublished(),
                bookRequestDto.getAuthors(),
                bookRequestDto.getPublishers(),
                bookRequestDto.getSubjects()
        );

        Book savedBook = bookRepository.save(book);

        return new BookResponseDto(savedBook);
    }

    public BookResponseDto updateBook(Long bookId, BookRequestDto bookRequestDto, UserDetailsImpl userDetails) {
        if(!validateUser(userDetails)){
            throw new AuthorizedAdminException();
        }

        Book book = bookRepository.findById(bookId).orElseThrow(
                FindBookException::new
        );

        book.update(
                bookRequestDto.getBookTitle(),
                bookRequestDto.getBookPublished()
        );

        Book savedBook = bookRepository.save(book);

        return new BookResponseDto(savedBook);
    }

    public Long deleteBook(Long bookId, UserDetailsImpl userDetails) {
        if(!validateUser(userDetails)){
            throw new AuthorizedAdminException();
        }

        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new FindBookException()
        );

        bookRepository.delete(book);

        return bookId;
    }

    public Boolean validateUser(UserDetailsImpl userDetails) {
        return userDetails.getUser().getRole().equals(UserRole.ROLE_ADMIN);
    }

    public List<BookResponseDtos> getBooks() {
        List<Book> books = bookRepository.findAll();

        return books.stream().map(BookResponseDtos::new).toList();
    }

    public BookResponseDto getBookById(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(
                FindBookException::new
        );
        return new BookResponseDto(book);
    }

//    public List<BookResponseDtos> getBooksByCategory(Long categoryId) {
//        BookCategory bookCategory = categoryRepository.findById(categoryId).orElseThrow(
//                () -> new FindCatogoryException()
//        );
//        List<Book> booksByCategory = bookRepository.findAllByCategory(bookCategory);
//        return booksByCategory.stream().map(BookResponseDtos::new).toList();
//    }
}
