package com.example.library_management.domain.book.cache.bookPublisher.service;

import com.example.library_management.domain.book.cache.bookPublisher.entity.BookPublisher;
import com.example.library_management.domain.book.cache.bookPublisher.repository.BookPublisherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookPublisherService {
    private final BookPublisherRepository publisherRepository;

    @Cacheable(value = "publishers", key = "#name")
    public BookPublisher getPublisherByName(String name) {
        // 캐시에 데이터가 없을 경우 데이터베이스에서 조회
        return publisherRepository.findByPublisherName(name).orElse(null);
    }

    @CachePut(value = "publishers", key = "#publisher.name")
    public BookPublisher savePublisher(BookPublisher publisher) {
        return publisherRepository.save(publisher); // 데이터베이스에 저장
    }

    @CacheEvict(value = "publishers", key = "#name")
    public void deletePublisherByName(String name) {
        publisherRepository.deleteByPublisherName(name); // 데이터베이스에서 삭제
    }
}
