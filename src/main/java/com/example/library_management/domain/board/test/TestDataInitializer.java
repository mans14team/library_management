package com.example.library_management.domain.board.test;

import com.example.library_management.domain.board.dto.request.BoardCreateRequestDto;
import com.example.library_management.domain.board.enums.BoardType;
import com.example.library_management.domain.board.service.BoardService;
import com.example.library_management.domain.user.entity.User;
import com.example.library_management.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Random;

@Configuration
@RequiredArgsConstructor
public class TestDataInitializer {
    private final BoardService boardService;
    private final UserRepository userRepository;
    private final Random random = new Random();

    @Bean
    @Profile("test") // test 프로필에서만 실행되도록 설정
    public CommandLineRunner initTestData() {
        return args -> {
            // 테스트용 관리자와 일반 사용자 조회
            User adminUser = userRepository.findByUserName("admin")
                    .orElseThrow(() -> new RuntimeException("Admin user not found"));
            User normalUser = userRepository.findByUserName("user")
                    .orElseThrow(() -> new RuntimeException("Normal user not found"));

            // 공지사항 100개 생성 (관리자 계정으로)
            for (int i = 1; i <= 100; i++) {
                BoardCreateRequestDto requestDto = createTestBoardDto(i, BoardType.NOTICE);
                boardService.createBoard(requestDto, adminUser);

                if (i % 5 == 0) {
                    System.out.println(i + "개의 공지사항이 생성되었습니다.");
                }
            }

            // 문의사항 100개 생성 (일반 사용자 계정으로)
            for (int i = 1; i <= 100; i++) {
                BoardCreateRequestDto requestDto = createTestBoardDto(i, BoardType.INQUIRY);
                boardService.createBoard(requestDto, normalUser);

                if (i % 10 == 0) {
                    System.out.println(i + "개의 문의사항이 생성되었습니다.");
                }
            }

            System.out.println("테스트 데이터 초기화가 완료되었습니다.");
        };
    }

    private BoardCreateRequestDto createTestBoardDto(int index, BoardType boardType) {
        // 게시글 제목 생성
        String title = generateTestTitle(index, boardType);

        // 게시글 내용 생성
        String content = generateTestContent(index, boardType);

        // 게시글 특성 설정
        boolean isSecret = boardType == BoardType.INQUIRY && random.nextInt(10) < 1; // 문의사항만 10% 확률로 비밀글
        boolean isPinned = boardType == BoardType.NOTICE && random.nextInt(10) < 1; // 공지사항만 10% 확률로 고정글

        // DTO 생성 및 값 설정
        BoardCreateRequestDto requestDto = new BoardCreateRequestDto();
        setFieldValue(requestDto, "title", title);
        setFieldValue(requestDto, "content", content);
        setFieldValue(requestDto, "boardType", boardType);
        setFieldValue(requestDto, "isSecret", isSecret);
        setFieldValue(requestDto, "isPinned", isPinned);

        return requestDto;
    }

    // Reflection을 사용하여 private 필드에 값을 설정
    private void setFieldValue(BoardCreateRequestDto dto, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = BoardCreateRequestDto.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(dto, value);
        } catch (Exception e) {
            throw new RuntimeException("필드 설정 중 오류 발생: " + fieldName, e);
        }
    }

    private String generateTestTitle(int index, BoardType boardType) {
        String[] noticePrefixes = {"공지", "안내", "긴급", "중요", "알림"};
        String[] inquiryPrefixes = {"문의", "요청", "건의", "질문", "확인"};
        String[] topics = {"도서관 이용", "대출", "반납", "시설", "프로그램", "강좌", "이벤트"};

        String[] prefixes = boardType == BoardType.NOTICE ? noticePrefixes : inquiryPrefixes;
        String prefix = prefixes[random.nextInt(prefixes.length)];
        String topic = topics[random.nextInt(topics.length)];

        return String.format("[%s] %s 관련 %s #%d",
                prefix,
                topic,
                boardType == BoardType.NOTICE ? "공지사항" : "문의사항",
                index);
    }

    private String generateTestContent(int index, BoardType boardType) {
        StringBuilder content = new StringBuilder();

        // 기본 내용
        content.append("이 게시글은 테스트를 위해 자동으로 생성된 ")
                .append(index)
                .append("번째 ")
                .append(boardType == BoardType.NOTICE ? "공지사항" : "문의사항")
                .append("입니다.\n\n");

        // 게시글 타입에 따른 내용 생성
        if (boardType == BoardType.NOTICE) {
            // 공지사항용 내용
            int paragraphs = random.nextInt(3) + 2; // 2~4 단락
            for (int i = 0; i < paragraphs; i++) {
                content.append("공지사항 단락 ").append(i + 1).append(": ");
                content.append("도서관 이용자 여러분께 안내드립니다. ");
                content.append("원활한 도서관 운영을 위해 다음 사항을 숙지해 주시기 바랍니다. ");
                content.append("자세한 내용은 도서관 안내데스크에서 확인하실 수 있습니다.\n\n");
            }
        } else {
            // 문의사항용 내용
            int paragraphs = random.nextInt(2) + 1; // 1~2 단락
            for (int i = 0; i < paragraphs; i++) {
                content.append("문의사항 단락 ").append(i + 1).append(": ");
                content.append("도서관 이용 중 문의사항이 있어 글을 남깁니다. ");
                content.append("확인 후 답변 부탁드립니다. ");
                content.append("추가 문의사항이 있다면 연락 바랍니다.\n\n");
            }
        }

        return content.toString();
    }
}
