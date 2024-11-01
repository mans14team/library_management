package com.example.library_management.domain.book.cache;

import com.example.library_management.domain.book.cache.bookAuthor.service.BookAuthorService;
import com.example.library_management.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cache")
@RequiredArgsConstructor
public class CachingController {
    private final BookAuthorService authorService;
    @DeleteMapping
    public ResponseEntity deleteCache(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        authorService.clearCache();
        return ResponseEntity.ok().build();
    }
}
