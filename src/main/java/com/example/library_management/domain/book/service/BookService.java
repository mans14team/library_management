package com.example.library_management.domain.book.service;

import com.example.library_management.domain.book.cache.EntityService;
import com.example.library_management.domain.book.cache.bookAuthor.entity.BookAuthor;
import com.example.library_management.domain.book.cache.bookAuthor.service.BookAuthorService;
import com.example.library_management.domain.book.cache.bookPublisher.entity.BookPublisher;
import com.example.library_management.domain.book.cache.bookPublisher.service.BookPublisherService;
import com.example.library_management.domain.book.cache.bookSubject.entity.BookSubject;
import com.example.library_management.domain.book.cache.bookSubject.service.BookSubjectService;
import com.example.library_management.domain.book.dto.BookResponseDto;
import com.example.library_management.domain.book.dto.BookRequestDto;
import com.example.library_management.domain.book.dto.BookResponseDtos;
import com.example.library_management.domain.book.entity.Book;
import com.example.library_management.domain.book.exception.AuthorizedAdminException;
import com.example.library_management.domain.book.exception.CustomDuplicateException;
import com.example.library_management.domain.book.exception.FindBookException;
import com.example.library_management.domain.book.repository.BookRepository;
import com.example.library_management.domain.user.enums.UserRole;
import com.example.library_management.global.security.UserDetailsImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final BookAuthorService bookAuthorService;
    private final BookPublisherService bookPublisherService;
    private final BookSubjectService bookSubjectService;

    private static final Logger logger = LoggerFactory.getLogger(BookService.class);


    @Transactional
    public BookResponseDto addBook(BookRequestDto bookRequestDto, UserDetailsImpl userDetails) {
        if(!validateUser(userDetails)){
            throw new AuthorizedAdminException();
        }

        Book book = new Book(
                bookRequestDto.getIsbn(),
                bookRequestDto.getBookTitle(),
                bookRequestDto.getBookPublished()
        );


//        if(!bookRequestDto.getBookAuthor().isEmpty()) {
//            Set<BookAuthor> newAuthors = new HashSet<>();
//            Set<BookAuthor> existingAuthors = new HashSet<>();
//            for(String authorName : bookRequestDto.getBookAuthor()){
//                BookAuthor author = bookAuthorService.getAuthorFromCache(authorName);
//
//                if(author == null){
//                    author = new BookAuthor(authorName);
//                    author = bookAuthorService.updateCache(author);
//                    newAuthors.add(author);
//                } else {
//                    existingAuthors.add(author);
//                }
//            }
//
//            if(!newAuthors.isEmpty()){
//                List<BookAuthor> savedAuthors = bookAuthorService.saveAuthors(newAuthors);
//                book.addAuthors(new HashSet<>(savedAuthors));
//            }
//
//            book.addAuthors(existingAuthors);
//
//        }
        if(!bookRequestDto.getBookAuthor().isEmpty()) {
            Set<BookAuthor> authors = processEntities(new HashSet<>(bookRequestDto.getBookAuthor()), bookAuthorService);
            book.addAuthors(authors);
        }

        Book savedBook = bookRepository.save(book);
        return new BookResponseDto(savedBook);
    }

    private <T> Set<T> processEntities(Set<String> entityNames, EntityService<T> service) {
        Set<T> newEntities = new HashSet<>();
        Set<T> existingEntities = new HashSet<>();

        for (String name : entityNames) {
            T entity = service.getEntityFromCache(name);

            if (entity == null) {
                entity = service.createEntity(name);
                newEntities.add(entity);
            } else {
                existingEntities.add(entity);
            }
        }

        if (!newEntities.isEmpty()) {
            List<T> savedEntities = service.saveEntities(newEntities);
            newEntities.clear();
            newEntities.addAll(savedEntities);
        }

        newEntities.addAll(existingEntities); // 새로운 것과 기존 것을 합칩니다.

        return newEntities;
    }

//    @Transactional
//    public BookResponseDto addBook(BookRequestDto bookRequestDto, UserDetailsImpl userDetails) {
//        if(!validateUser(userDetails)){
//            throw new AuthorizedAdminException();
//        }
//
//        Book book = new Book(
//                bookRequestDto.getIsbn(),
//                bookRequestDto.getBookTitle(),
//                bookRequestDto.getBookPublished()
//        );
//
//        // 저장시 unique 지정된 isbn 이 중복될 경우 발생하는 예외 캐치해서 예외 처리.
//        // 아직 익셉션 핸들러 안한거 같음.
//        try {
//            Book savedBook = bookRepository.save(book);
//            return new BookResponseDto(savedBook);
//        } catch (DataIntegrityViolationException e) {
//            throw new CustomDuplicateException(book.getIsbn());
//        }
//    }

    @Transactional
    public BookResponseDto updateBook(Long bookId, BookRequestDto bookRequestDto, UserDetailsImpl userDetails) {
        if(!validateUser(userDetails)){
            throw new AuthorizedAdminException();
        }

        Book book = bookRepository.findById(bookId).orElseThrow(
                FindBookException::new
        );

        // 도서 정보 수정 시, 섭젝, 퍼블리셔, 저자는 추가, 삭제할 요소 각각 지정받기.
        logger.info("aaa");
        book.update(
            bookRequestDto.getIsbn(),
            bookRequestDto.getBookTitle(),
            bookRequestDto.getBookPublished()
        );

        Book savedBook = bookRepository.save(book);

        logger.info("asdfg");

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
}
