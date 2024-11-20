package com.example.library_management.domain.review.service;

import com.example.library_management.domain.review.controller.ReviewController;
import com.example.library_management.domain.review.dto.request.ReviewSaveRequest;
import com.example.library_management.domain.review.dto.response.ReviewSaveResponse;
import com.example.library_management.domain.user.entity.User;
import com.example.library_management.domain.user.enums.UserRole;
import com.example.library_management.global.config.SecurityConfig;
import com.example.library_management.global.jwt.JwtUtil;
import com.example.library_management.global.security.JwtAuthenticationFilter;
import com.example.library_management.global.security.JwtAuthorizationFilter;
import com.example.library_management.global.security.UserDetailsImpl;
import com.example.library_management.global.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ReviewController.class)
@AutoConfigureMockMvc
@Import({SecurityConfig.class})
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;
    @MockBean
    private UserDetailsImpl userDetailUser;
    @MockBean
    private ReviewService reviewService;

    @MockBean
    private RedisTemplate<String, String> redisTemplate;
    @MockBean
    private JwtUtil jwtUtil;
    @MockBean
    private UserDetailsServiceImpl userDetailsService;
    @MockBean
    private ReviewController reviewController;
    @MockBean
    private AuthenticationConfiguration authenticationConfiguration;
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean
    private JwtAuthorizationFilter jwtAuthorizationFilter;


    @BeforeEach
    void setUp() {
        User user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(user, "userName", "testUser");
        ReflectionTestUtils.setField(user, "role", UserRole.ROLE_USER);
        userDetailUser = new UserDetailsImpl(user);
    }
//todo
    @Test
    void 리뷰_생성_테스트_성공시201반환() throws Exception {

        //given
        Long bookId = 1L;
        ReviewSaveRequest request = new ReviewSaveRequest(
                5,
                "리뷰 제목",
                "리뷰내용"
        );

        ReviewSaveResponse response = new ReviewSaveResponse(
                request.getReviewStar(),
                request.getReviewTitle(),
                request.getReviewDescription());
        //when&then
        mockMvc.perform(post("/library/books/{bookId}/reviews", bookId)
                        .with(anonymous()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewStar").value(response.getReviewStar()))
                .andExpect(jsonPath("$.reviewTitle").value(response.getReviewTitle()))
                .andExpect(jsonPath("$.reviewDescription").value(response.getReviewDescription()));

    }


}
