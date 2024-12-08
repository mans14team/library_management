package com.example.library_management.domain.user.controller;

import com.example.library_management.domain.auth.exception.UnauthorizedPasswordException;
import com.example.library_management.domain.user.dto.request.UserChangePasswordRequestDto;
import com.example.library_management.domain.user.dto.request.UserCheckPasswordRequestDto;
import com.example.library_management.domain.user.dto.response.UserResponseDto;
import com.example.library_management.domain.user.entity.User;
import com.example.library_management.domain.user.enums.UserRole;
import com.example.library_management.domain.user.exception.NotFoundUserException;
import com.example.library_management.domain.user.service.UserService;
import com.example.library_management.global.jwt.JwtUtil;
import com.example.library_management.global.security.UserDetailsImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@MockBean(JpaMetamodelMappingContext.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    @WithMockUser
    void 회원_정보_조회_성공() throws Exception{
        // given
        Long userId = 1L;
        UserResponseDto responseDto = new UserResponseDto(userId, "test@example.com");
        when(userService.getUser(userId)).thenReturn(responseDto);

        // when & then
        mockMvc.perform(get("/library/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(userId))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andDo(print());
    }

    @Test
    @WithMockUser
    void 회원_정보_조회_실패_존재하지_않는_회원() throws Exception{
        // given
        Long userId = 999L;
        when(userService.getUser(userId)).thenThrow(new NotFoundUserException());

        // when & then
        mockMvc.perform(get("/library/user/{userId}", userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andDo(print());
    }

    @Test
    void 비밀번호_변경_성공() throws Exception{
        // given
        UserChangePasswordRequestDto requestDto = new UserChangePasswordRequestDto(
                "oldPassword123!",
                "newPassword123!"
        );
        User mockUser = mock(User.class);
        UserDetailsImpl userDetails = new UserDetailsImpl(mockUser);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userService.changePassword(any(User.class), eq(requestDto)))
                .thenReturn("비밀번호가 정상적으로 변경되었습니다.");

        // when & then
        mockMvc.perform(put("/library/user")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andDo(print());
    }

    @Test
    void 비밀번호_변경_실패_이전_비밀번호_불일치() throws Exception{
        // given
        UserChangePasswordRequestDto requestDto = new UserChangePasswordRequestDto(
                "wrongOldPassword",
                "newPassword123!"
        );

        User mockUser = mock(User.class);
        when(mockUser.getRole()).thenReturn(UserRole.ROLE_USER);

        UserDetailsImpl userDetails = new UserDetailsImpl(mockUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userService.changePassword(any(User.class), any(UserChangePasswordRequestDto.class)))
                .thenThrow(new UnauthorizedPasswordException());

        // when & then
        mockMvc.perform(put("/library/user")
                        .with(csrf())
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andDo(print());
    }

    @Test
    void 회원_탈퇴_성공() throws Exception{
        // given
        UserCheckPasswordRequestDto requestDto = new UserCheckPasswordRequestDto("password123!");

        User mockUser = mock(User.class);
        when(mockUser.getRole()).thenReturn(UserRole.ROLE_USER);

        UserDetailsImpl userDetails = new UserDetailsImpl(mockUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userService.deleteUser(any(User.class), eq(requestDto)))
                .thenReturn("회원탈퇴가 정상적으로 완료되었습니다.");

        // when & then
        mockMvc.perform(delete("/library/user")
                        .with(csrf())
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andDo(print());
    }

    @Test
    void 회원_탈퇴_실패_비밀번호_불일치() throws Exception{
        // given
        UserCheckPasswordRequestDto requestDto = new UserCheckPasswordRequestDto("wrongPassword");

        User mockUser = mock(User.class);
        when(mockUser.getRole()).thenReturn(UserRole.ROLE_USER);

        UserDetailsImpl userDetails = new UserDetailsImpl(mockUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userService.deleteUser(any(User.class), any(UserCheckPasswordRequestDto.class)))
                .thenThrow(new UnauthorizedPasswordException());

        // when & then
        mockMvc.perform(delete("/library/user")
                        .with(csrf())
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andDo(print());
    }
}