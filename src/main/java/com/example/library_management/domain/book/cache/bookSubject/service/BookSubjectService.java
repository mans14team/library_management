package com.example.library_management.domain.book.cache.bookSubject.service;

import com.example.library_management.domain.book.cache.bookAuthor.entity.BookAuthor;
import com.example.library_management.domain.book.cache.bookSubject.entity.BookSubject;
import com.example.library_management.domain.book.cache.bookSubject.repository.BookSubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookSubjectService {
    private final BookSubjectRepository subjectRepository;

    @Cacheable(value = "authors", key = "#name")
    public BookSubject getSubjectByName(String name) {
        // 캐시에 데이터가 없을 경우 데이터베이스에서 조회
        return subjectRepository.findBySubjectName(name).orElse(null);
    }

    @CachePut(value = "authors", key = "#subject.name")
    public BookSubject saveAuthor(BookSubject subject) {
        return subjectRepository.save(subject); // 데이터베이스에 저장
    }

    @CacheEvict(value = "authors", key = "#name")
    public void deleteSubjectByName(String name) {
        subjectRepository.deleteBySubjectName(name); // 데이터베이스에서 삭제
    }
}

