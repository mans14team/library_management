package com.example.library_management.domain.book.repository;

import com.example.library_management.domain.book.entity.Book;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.library_management.domain.book.entity.QBook.book;

@Repository
@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Book> searchBooks(String isbn, String bookTitle, String author, String publisher, List<String> subjects, Pageable pageable) {
        List<Book> results = queryFactory
                .selectFrom(book)
                .where(
                        eqIsbn(isbn),
                        eqBookTitle(bookTitle),
                        eqAuthor(author),
                        eqPublisher(publisher),
                        containsSubjects(subjects)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectFrom(book)
                .where(
                        eqIsbn(isbn),
                        eqBookTitle(bookTitle),
                        eqAuthor(author),
                        eqPublisher(publisher),
                        containsSubjects(subjects)
                )
                .fetchCount();

        return new PageImpl<>(results, pageable, total);

    }

    private BooleanExpression eqIsbn(String isbn) {
        return StringUtils.isNotBlank(isbn) ? book.isbn.containsIgnoreCase(isbn) : null;
    }

    private BooleanExpression eqBookTitle(String bookTitle) {
        return StringUtils.isNotBlank(bookTitle) ? book.bookTitle.containsIgnoreCase(bookTitle) : null;
    }

    private BooleanExpression eqAuthor(String author) {
        return StringUtils.isNotBlank(author) ? book.authors.any().containsIgnoreCase(author) : null;
    }

    private BooleanExpression eqPublisher(String publisher) {
        return StringUtils.isNotBlank(publisher) ? book.publishers.any().containsIgnoreCase(publisher) : null;
    }

    private BooleanExpression containsSubjects(List<String> subjects) {
        return (subjects != null && !subjects.isEmpty()) ? book.subjects.any().in(subjects) : null;
    }

}
