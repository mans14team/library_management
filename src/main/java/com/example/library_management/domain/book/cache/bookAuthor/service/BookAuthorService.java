package com.example.library_management.domain.book.cache.bookAuthor.service;

import com.example.library_management.domain.book.cache.EntityService;
import com.example.library_management.domain.book.cache.bookAuthor.entity.BookAuthor;
import com.example.library_management.domain.book.cache.bookAuthor.repository.BookAuthorRepository;
import com.example.library_management.domain.book.cache.bookPublisher.entity.BookPublisher;
import com.example.library_management.domain.book.service.BookService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BookAuthorService implements EntityService<BookAuthor> {
    private final BookAuthorRepository authorRepository;

    @Cacheable(value = "authors", key = "#name")
    public BookAuthor getEntityFromCache(String name) {
        return authorRepository.findByAuthorName(name).orElse(null);
        // 캐시에 없을 경우 데이터베이스에서 조회 후 반환
    }

    public BookAuthor createEntity(String name) {
        return new BookAuthor(name);
    }

    public List<BookAuthor> saveEntities(Set<BookAuthor> authors) {
        List<BookAuthor> savedAuthors = authorRepository.saveAll(authors);
        for(BookAuthor author : savedAuthors){
            updateCache(author);
        }


        return savedAuthors;
    }

//    @CachePut(value = "authors", key = "#author.authorName")
//    public List<BookAuthor> updateCache(Set<BookAuthor> authors) {
//        return authorRepository.saveAll(authors);// 데이터베이스에 저장
//    }

    @CachePut(value = "authors", key = "#author.name")
    public BookAuthor updateCache(BookAuthor author) {
        return author; // 데이터베이스에 저장
    }

    @CacheEvict(value = "authors", key = "#name")
    public void deleteAuthorByName(String name) {
        authorRepository.deleteByAuthorName(name); // 데이터베이스에서 삭제
    }

    @CacheEvict(value = "authors", allEntries = true)
    public void clearCache() {
        // 캐시를 비우는 메서드
    }

    public List<BookAuthor> getAllEntitiesFromDatabase() {
        return authorRepository.findAll(); // 모든 저자 정보 조회
    }
}

