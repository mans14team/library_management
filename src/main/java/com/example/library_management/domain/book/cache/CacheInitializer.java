package com.example.library_management.domain.book.cache;

import com.example.library_management.domain.book.cache.bookAuthor.entity.BookAuthor;
import com.example.library_management.domain.book.cache.bookAuthor.service.BookAuthorService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

@Component
public class CacheInitializer {

    private static final Logger logger = LoggerFactory.getLogger(CacheInitializer.class);

    private final BookAuthorService bookAuthorService;

    public CacheInitializer(BookAuthorService bookAuthorService) {
        this.bookAuthorService = bookAuthorService;
    }

    @PostConstruct
    public void init() {
        logger.debug("되냐?");
        initializeCache();
    }

    private void initializeCache() {
        try {
            List<BookAuthor> authors = bookAuthorService.getAllEntitiesFromDatabase();
//            bookAuthorService.updateCache(new HashSet<>(authors));
            for(BookAuthor author : authors){
                bookAuthorService.updateCache(author);
            }
            logger.info("Cache initialized successfully with {} authors.", authors.size());
        } catch (Exception e) {
            logger.error("Error initializing cache: {}", e.getMessage(), e);
        }
    }
}
