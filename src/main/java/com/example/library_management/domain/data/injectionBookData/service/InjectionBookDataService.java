package com.example.library_management.domain.data.injectionBookData.service;

import com.example.library_management.domain.book.entity.Book;
import com.example.library_management.domain.book.exception.AuthorizedAdminException;
import com.example.library_management.domain.book.repository.BookRepository;
import com.example.library_management.domain.data.injectionBookData.dto.InjectionBookDataDto;
import com.example.library_management.domain.user.enums.UserRole;
import com.example.library_management.global.security.UserDetailsImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InjectionBookDataService {

    private final BookRepository bookRepository;

    @Transactional
    public void injectionBookData(UserDetailsImpl userDetails) throws IOException {
        if (!validateUser(userDetails)) {
            throw new AuthorizedAdminException();
        }

        File directory = new File("src/main/resources/data/jsons");

        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException("The provided path is not a valid directory.");
        }

        File[] jsonFiles = directory.listFiles((dir, name) -> name.endsWith(".json"));
        if (jsonFiles == null || jsonFiles.length == 0) {
            throw new IllegalArgumentException("No JSON files found in the directory.");
        }

        // Set으로 중복 검사 최적화
        Set<String> existingIsbns = new HashSet<>(bookRepository.findAllIsbns());

        for (File jsonFile : jsonFiles) {
            processJsonFile(jsonFile, existingIsbns);
        }
    }

    private void processJsonFile(File jsonFile, Set<String> existingIsbns) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        List<InjectionBookDataDto> dtoList = objectMapper.readValue(
                jsonFile,
                new TypeReference<List<InjectionBookDataDto>>() {}
        );

        // JSON 데이터 내 중복을 제거하면서, 기존 데이터와 중복된 ISBN 필터링
        List<Book> entitiesToSave = dtoList.stream()
                .filter(dto -> existingIsbns.add(dto.getIsbn()))  // Set에 추가되면 새로운 ISBN
                .map(this::convertToEntity)
                .collect(Collectors.toList());

        if (!entitiesToSave.isEmpty()) {
            List<Book> savedBooks = bookRepository.saveAll(entitiesToSave);
            savedBooks.forEach(book -> existingIsbns.add(book.getIsbn()));
        }
    }

    private Book convertToEntity(InjectionBookDataDto dto) {
        return new Book(
                dto.getIsbn(),
                dto.getBookTitle(),
                dto.getBookPublished(),
                dto.getAuthors().stream().limit(5).collect(Collectors.toList()),  // 최대 5개 제한
                dto.getPublishers().stream().limit(5).collect(Collectors.toList()),
                dto.getSubjects().stream().limit(5).collect(Collectors.toList())
        );
    }

    public Boolean validateUser(UserDetailsImpl userDetails) {
        return userDetails.getUser().getRole().equals(UserRole.ROLE_ADMIN);
    }

}
