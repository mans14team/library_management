package com.example.library_management.domain.data.catagoryData;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SubjectAnalysisService {

    private final ObjectMapper objectMapper;

    public SubjectAnalysisService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void analyzeSubjectsAndSaveToJson() throws IOException {
        // selected_books.json 파일 경로
        File file = new File("selected_books.json");
        JsonNode books = objectMapper.readTree(file);
        Map<String, Integer> subjectCount = new HashMap<>();

        for (JsonNode book : books) {
            if (book.has("subject")) {
                List<String> subjects = objectMapper.convertValue(book.get("subject"), List.class);
                for (String subject : subjects) {
                    subjectCount.put(subject, subjectCount.getOrDefault(subject, 0) + 1);
                }
            }
        }

        // 주제를 내림차순으로 정렬
        List<Map.Entry<String, Integer>> sortedSubjects = subjectCount.entrySet()
                .stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .collect(Collectors.toList());

        // 결과를 JSON 파일로 저장
        File jsonOutputFile = new File("subject_count.json");
        objectMapper.writeValue(jsonOutputFile, sortedSubjects);
    }
}
