package com.example.library_management.global.config;

import com.example.library_management.domain.common.exception.GlobalException;
import com.example.library_management.domain.common.exception.GlobalExceptionConst;
import com.example.library_management.domain.user.entity.User;
import com.example.library_management.domain.user.enums.UserRole;
import com.example.library_management.global.jwt.JwtUtil;
import com.example.library_management.global.oauth.handler.OAuth2LoginFailureHandler;
import com.example.library_management.global.oauth.handler.OAuth2LoginSuccessHandler;
import com.example.library_management.global.oauth.service.CustomOAuth2UserService;
import com.example.library_management.global.security.UserDetailsImpl;
import com.example.library_management.global.security.UserDetailsServiceImpl;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private CustomOAuth2UserService customOAuth2UserService;

    @MockBean
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @MockBean
    private OAuth2LoginFailureHandler oAuth2LoginFailureHandler;

    @Test
    void 인증되지_않은_사용자는_보호된_엔드포인트에_접근할_수_없음() throws Exception{
        // given
        when(jwtUtil.getJwtFromHeader(any()))
                .thenThrow(new GlobalException(GlobalExceptionConst.UNAUTHORIZED_OWNERTOKEN));

        // when & then
        mockMvc.perform(get("/library/test/secured")
                        .header("Authorization", "Bearer invalid_token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.data.status").value(401))
                .andExpect(jsonPath("$.data.message").value(containsString("유저 토큰이 틀렸습니다")))
                .andDo(print());
    }

    @Test
    @WithMockUser
    void 인증된_사용자는_보호된_엔드포인트에_접근할_수_있다() throws Exception{
        // given
        String validToken = "valid_token";
        String userEmail = "test@test.com";

        // JWT 토큰 검증 모의
        when(jwtUtil.getJwtFromHeader(any())).thenReturn(validToken);
        when(jwtUtil.validateToken(validToken)).thenReturn(true);
        when(jwtUtil.getUserInfoFromToken(validToken))
                .thenReturn(Jwts.claims().setSubject(userEmail));

        // UserDetails 모의
        User user = mock(User.class);
        when(user.getEmail()).thenReturn(userEmail);
        when(user.getRole()).thenReturn(UserRole.ROLE_USER);

        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
        when(userDetails.getUsername()).thenReturn(userEmail);
        when(userDetails.getUser()).thenReturn(user);

        when(userDetailsService.loadUserByUsername(userEmail))
                .thenReturn(userDetails);

        // when & then
        mockMvc.perform(get("/library/test/secured")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void 공개_엔드포인드는_인증_없이_접근_가능하다() throws Exception{
        mockMvc.perform(get("/library/test/public"))
                .andExpect(status().isOk())
                .andDo(print());
    }
}