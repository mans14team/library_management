package com.example.library_management.domain.data.injectionBookData.service;

import com.example.library_management.domain.book.entity.Book;
import com.example.library_management.domain.book.exception.AuthorizedAdminException;
import com.example.library_management.domain.book.repository.BookRepository;
import com.example.library_management.domain.data.injectionBookData.dto.InjectionBookDataDto;
import com.example.library_management.domain.user.enums.UserRole;
import com.example.library_management.global.security.UserDetailsImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InjectionBookDataService {
    private final BookRepository bookRepository;

    public void injectionBookData(String json, UserDetailsImpl userDetails) throws IOException{
        if(!validateUser(userDetails)){
            throw new AuthorizedAdminException();
        }

        ObjectMapper objectMapper = new ObjectMapper();

        List<InjectionBookDataDto> dtoList = objectMapper.readValue(
                new File(json),
                new TypeReference<List<InjectionBookDataDto>>() {}
        );

        List<Book> entities = dtoList.stream()
                .map(this::convertToEntity)
                .toList();

        bookRepository.saveAll(entities);
    }

    private Book convertToEntity(InjectionBookDataDto dto) {
        Book book = new Book(
                dto.getIsbn(),
                dto.getBookTitle(),
                dto.getBookPublished(),
                dto.getAuthors().subList(0, Math.min(dto.getAuthors().size(), 5)),
                dto.getPublishers().subList(0, Math.min(dto.getPublishers().size(), 5)),
                dto.getSubjects().subList(0, Math.min(dto.getSubjects().size(), 5))
        );

        return book;
    }

    public Boolean validateUser(UserDetailsImpl userDetails) {
        return userDetails.getUser().getRole().equals(UserRole.ROLE_ADMIN);
    }

    private void validateListSize(List<String> list, int maxSize, String listName) {
        if (list.size() > maxSize) {
            throw new IllegalArgumentException("The number of " + listName + " exceeds the allowed limit: " + maxSize);
        }
    }

}
