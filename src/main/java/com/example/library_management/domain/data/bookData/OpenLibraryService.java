package com.example.library_management.domain.data.bookData;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OpenLibraryService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String BASE_URL = "https://openlibrary.org/search.json";

    public OpenLibraryService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);  // JSON 정렬
    }

    public void fetchAndSaveSelectedFields(String subject, int totalBooks) {
        int page = 1;
        int booksFetched = 0;

        List<Map<String, Object>> selectedBooks = new ArrayList<>();

        // 현재 날짜와 시간으로 파일 이름을 생성
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        String fileName = "selected_books_" + timestamp + ".json";

        try (FileWriter fileWriter = new FileWriter(fileName)) {
            while (booksFetched < totalBooks) {
                // API 요청 URL 생성
                String url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                        .queryParam("q", "subject:" + subject)
                        .queryParam("page", page)
                        .toUriString();

                // API 호출
                Map<String, Object> response = restTemplate.getForObject(url, Map.class);
                List<Map<String, Object>> books = (List<Map<String, Object>>) response.get("docs");

                if (books == null || books.isEmpty()) {
                    System.out.println("더 이상 데이터를 가져올 수 없습니다.");
                    break;
                }

                // 필요한 필드만 추출하여 리스트에 저장
                for (Map<String, Object> book : books) {
                    String bookKey = (String) book.get("key");
                    Map<String, Object> selectedBook = Map.of(
                            "bookKey", bookKey,
                            "bookTitle", book.get("title"),
                            "bookAuthor", book.get("author_name"),
                            "bookPublished", book.get("first_publish_year"),
                            "bookSubject", book.get("subject"),
                            "bookPublisher", book.get("publisher")
                    );
                    selectedBooks.add(selectedBook);
                    booksFetched++;

                    if (booksFetched >= totalBooks) break;
                }

                System.out.println("Fetched page " + page + ", Total books fetched: " + booksFetched);
                page++;

                // 딜레이 추가
                Thread.sleep(500);  // 0.5초 대기
            }

            // 결과를 JSON 파일로 저장
            objectMapper.writeValue(fileWriter, selectedBooks);
        } catch (Exception e) {
            System.out.println("Error fetching data: " + e.getMessage());
        }
    }
}



