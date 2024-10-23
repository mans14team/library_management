package com.example.library_management.domain.book.service;

import com.example.library_management.domain.book.dto.BookResponseDto;
import com.example.library_management.domain.book.dto.BookRequestDto;
import com.example.library_management.domain.book.dto.BookResponseDtos;
import com.example.library_management.domain.book.entity.Book;
import com.example.library_management.domain.book.exception.AuthorizedAdminException;
import com.example.library_management.domain.book.exception.FindBookException;
import com.example.library_management.domain.book.exception.FindCatogoryException;
import com.example.library_management.domain.book.repository.BookRepository;
import com.example.library_management.domain.bookCategory.entity.BookCategory;
import com.example.library_management.domain.bookCategory.repository.BookCategoryRepository;
import com.example.library_management.domain.user.enums.UserRole;
import com.example.library_management.global.security.UserDetailsImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookService {
    private final BookRepository bookRepository;
    private final BookCategoryRepository categoryRepository;

    public BookResponseDto addBook(BookRequestDto bookRequestDto, UserDetailsImpl userDetails) {
        if(!validateUser(userDetails)){
            throw new AuthorizedAdminException();
        }

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
                                FindCatogoryException::new
                        )
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

        if(bookRequestDto.getBookTitle() != null) {
            book.setBookTitle(bookRequestDto.getBookTitle());
        }
        if(bookRequestDto.getBookDescription() != null) {
            book.setBookDescription(bookRequestDto.getBookDescription());
        }
        if(bookRequestDto.getBookAuthor() != null) {
            book.setBookAuthor(bookRequestDto.getBookAuthor());
        }
        if(bookRequestDto.getBookPublished() != null) {
            book.setBookPublished(bookRequestDto.getBookPublished());
        }
        if(bookRequestDto.getBookPublisher() != null) {
            book.setBookPublisher(bookRequestDto.getBookPublisher());
        }
        if(bookRequestDto.getCategoryId() != null) {
            book.setCategory(categoryRepository.findById(bookRequestDto.getCategoryId()).orElseThrow(
                    () -> new FindCatogoryException()
            ));
        }

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
        return null;
    }

    public List<BookResponseDtos> getBooksByCategory(Long categoryId) {
        BookCategory bookCategory = categoryRepository.findById(categoryId).orElseThrow(
                () -> new FindCatogoryException()
        );
        List<Book> booksByCategory = bookRepository.findAllByCategory(bookCategory);
        return booksByCategory.stream().map(BookResponseDtos::new).toList();
    }
}
