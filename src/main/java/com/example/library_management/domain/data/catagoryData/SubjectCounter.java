package com.example.library_management.domain.data.catagoryData;

import org.springframework.web.client.RestTemplate;

import java.util.*;

public class SubjectCounter {
    private static final String BASE_URL = "https://openlibrary.org/search.json";

    public Map<String, Integer> fetchSubjects(int limit) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(BASE_URL + "?q=*&limit=" + limit, Map.class);
        List<Map<String, Object>> books = (List<Map<String, Object>>) response.get("docs");

        Map<String, Integer> subjectCount = new HashMap<>();

        // 각 도서의 subject 수집 및 집계
        for (Map<String, Object> book : books) {
            List<String> subjects = (List<String>) book.get("subject");
            if (subjects != null) {
                for (String subject : subjects) {
                    subjectCount.put(subject, subjectCount.getOrDefault(subject, 0) + 1);
                }
            }
        }

        return subjectCount; // 주제와 그 카운트를 반환
    }

    public static void main(String[] args) {
        SubjectCounter counter = new SubjectCounter();
        Map<String, Integer> subjectCounts = counter.fetchSubjects(10000); // 10,000건의 도서 정보 조회

        // 각 주제와 그 카운트 출력
        subjectCounts.forEach((subject, count) -> {
            System.out.println("Subject: " + subject + ", Count: " + count);
        });
    }
}
