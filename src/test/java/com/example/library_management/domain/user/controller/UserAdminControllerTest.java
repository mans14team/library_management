package com.example.library_management.domain.user.controller;

import com.example.library_management.domain.user.dto.request.UserRoleChangeRequestDto;
import com.example.library_management.domain.user.exception.NotFoundUserException;
import com.example.library_management.domain.user.service.UserAdminService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserAdminController.class)
@EnableMethodSecurity
@MockBean(JpaMetamodelMappingContext.class)
class UserAdminControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserAdminService userAdminService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void 관리자가_일반_사용자로_역할_변경_성공() throws Exception{
        // given
        long userId = 1L;
        UserRoleChangeRequestDto requestDto = new UserRoleChangeRequestDto(false, null);
        String expectedResponse = "User role changed to ROLE_USER";

        when(userAdminService.changeUserRole(eq(userId), any(UserRoleChangeRequestDto.class)))
                .thenReturn(expectedResponse);

        // when & then
        mockMvc.perform(patch("/library/admin/user/{userId}", userId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").value(expectedResponse))
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 일반_사용자가_관리자로_역할_변경_성공() throws Exception{
        // given
        long userId = 1L;
        String ownerToken = "validToken";
        UserRoleChangeRequestDto requestDto = new UserRoleChangeRequestDto(true, ownerToken);
        String expectedResponse = "User role changed to ROLE_ADMIN";

        when(userAdminService.changeUserRole(eq(userId), any(UserRoleChangeRequestDto.class)))
                .thenReturn(expectedResponse);

        // when & then
        mockMvc.perform(patch("/library/admin/user/{userId}", userId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "USER")
    void 권한이_없는_사용자의_역할_변경_실패() throws Exception{
        // given
        long userId = 1L;
        UserRoleChangeRequestDto requestDto = new UserRoleChangeRequestDto(false, null);

        // when & then
        mockMvc.perform(patch("/library/admin/user/{userId}", userId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 존재하지_않는_사용자_약할_변경_실패() throws Exception{
        // given
        long userId = 999L;
        UserRoleChangeRequestDto requestDto = new UserRoleChangeRequestDto(false, null);

        when(userAdminService.changeUserRole(eq(userId), any(UserRoleChangeRequestDto.class)))
                .thenThrow(new NotFoundUserException());

        // when & then
        mockMvc.perform(patch("/library/admin/user/{userId}", userId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
}