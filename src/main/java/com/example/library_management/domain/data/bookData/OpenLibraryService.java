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
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // JSON 정렬
    }

    public void fetchAndSaveSelectedFields(int totalBooks, String directoryPath) {
        int page = 1;
        int booksFetched = 0;
        int chunkSize = 1000; // 한 번에 가져올 데이터 수

        while (booksFetched < totalBooks) {
            try {
                // API 요청 URL 생성
                String url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                        .queryParam("q", "subject:" + "*") // 모든 주제 검색
                        .queryParam("page", page)
                        .queryParam("limit", chunkSize) // 요청당 limit 설정
                        .toUriString();

                // API 호출
                Map<String, Object> response = restTemplate.getForObject(url, Map.class);
                List<Map<String, Object>> books = (List<Map<String, Object>>) response.get("docs");

                if (books == null || books.isEmpty()) {
                    System.out.println("더 이상 데이터를 가져올 수 없습니다. 페이지: " + page);
                    break;
                }

                // 필요한 필드만 추출
                List<Map<String, Object>> selectedBooks = new ArrayList<>();
                for (Map<String, Object> book : books) {
                    Map<String, Object> selectedBook = Map.of(
                            "isbn", book.get("key"),
                            "bookTitle", book.get("title"),
                            "bookPublished", book.get("first_publish_year"),
                            "authors", book.get("author_name"),
                            "publishers", book.get("publisher"),
                            "subjects", book.get("subject")
                    );
                    selectedBooks.add(selectedBook);
                }

                // 가져온 데이터를 청크 단위로 저장
                saveToFile(selectedBooks, directoryPath, booksFetched / chunkSize + 1);
                booksFetched += selectedBooks.size();
                page++;
                System.out.println("Fetched page " + page + ", Total books fetched: " + booksFetched);

                // 딜레이 추가
                Thread.sleep(500); // API 서버 부하 방지

            } catch (Exception e) {
                System.err.println("Error fetching data on page " + page + ": " + e.getMessage());
                page++; // 다음 페이지로 넘어감
            }
        }
    }

    /**
     * 청크 데이터를 파일에 저장
     */
    private void saveToFile(List<Map<String, Object>> books, String directoryPath, int chunkNumber) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        String fileName = "books_chunk_" + chunkNumber + "_" + timestamp + ".json";
        String filePath = directoryPath + "/" + fileName;

        try (FileWriter fileWriter = new FileWriter(filePath)) {
            objectMapper.writeValue(fileWriter, books);
            System.out.println("Books chunk saved to " + filePath);
        } catch (IOException e) {
            System.err.println("Error saving chunk to file: " + e.getMessage());
        }
    }
}


