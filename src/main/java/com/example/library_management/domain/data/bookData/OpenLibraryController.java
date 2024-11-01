package com.example.library_management.domain.data.bookData;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OpenLibraryController {

    private final OpenLibraryService openLibraryService;

    public OpenLibraryController(OpenLibraryService openLibraryService) {
        this.openLibraryService = openLibraryService;
    }

    @GetMapping("api/fetch-books")
    public String fetchBooks(@RequestParam String subject, @RequestParam int count) {
        openLibraryService.fetchAndSaveSelectedFields(subject, count);
        return "Book data fetching initiated. Check books.json file for results.";
    }
}
