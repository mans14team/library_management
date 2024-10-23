package com.example.library_management.domain.book.service;

import com.example.library_management.domain.book.dto.BookReponseDto;
import com.example.library_management.domain.book.dto.BookRequestDto;
import com.example.library_management.domain.book.entity.Book;
import com.example.library_management.domain.book.exception.FindBookException;
import com.example.library_management.domain.book.exception.FindCatogoryException;
import com.example.library_management.domain.book.repository.BookRepository;
import com.example.library_management.domain.bookCategory.repository.BookCategoryRepository;
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

    public BookReponseDto updateBook(Long bookId, BookRequestDto bookRequestDto, UserDetailsImpl userDetails) {
        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new FindBookException()
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

        return new BookReponseDto(savedBook);
    }

    public Long deleteBook(Long bookId, UserDetailsImpl userDetails) {
        return 1L;
    }

    public Boolean validateUser(Long id, UserDetailsImpl userDetails) {
        return true;
    }

    public List<BookReponseDto> getBooks() {
        return null;
    }

    public BookReponseDto getBookById(Long bookId) {
        return null;
    }

    public List<BookReponseDto> getBooksByCategory(String category) {
        return null;
    }
}
