package com.example.library_management.domain.review.service;

import com.example.library_management.domain.review.controller.ReviewController;
import com.example.library_management.domain.review.dto.request.ReviewSaveRequest;
import com.example.library_management.domain.review.dto.response.ReviewSaveResponse;
import com.example.library_management.global.security.UserDetailsImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ReviewController.class)
@AutoConfigureMockMvc
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;
    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    void 리뷰_생성_테스트_성공시201반환() throws Exception {
        // given
        Long bookId = 1L;
        ReviewSaveRequest request = new ReviewSaveRequest(
                5,
                "리뷰 제목",
                "리뷰내용"
        );

        ReviewSaveResponse response = new ReviewSaveResponse(
                request.getReviewStar(),
                request.getReviewTitle(),
                request.getReviewDescription()
        );

        // Mock ReviewService response
        given(reviewService.saveReview(anyLong(), any(ReviewSaveRequest.class), any(UserDetailsImpl.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/library/books/{bookId}/reviews", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsBytes(request)))
                .andExpect(status().isCreated()) // 201 Created로 변경
                .andExpect(jsonPath("$.reviewStar").value(response.getReviewStar()))
                .andExpect(jsonPath("$.reviewTitle").value(response.getReviewTitle()))
                .andExpect(jsonPath("$.reviewDescription").value(response.getReviewDescription()));
    }
}




