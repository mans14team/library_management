package com.example.library_management.global.oauth.handler;

import com.example.library_management.domain.user.entity.User;
import com.example.library_management.global.jwt.JwtUtil;
import com.example.library_management.global.security.UserDetailsImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
@Slf4j(topic = "OAuth2 로그인 성공 핸들러")
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login 설공!");

        try {
            // OAuth2User를 UserDetailsImpl로 캐스팅
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            User user = userDetails.getUser();

            // 로그인에 성공한 경우 JWT 토큰 생성
            String accessToken = jwtUtil.createAccessToken(user.getEmail(), user.getRole());
            String refreshToken = jwtUtil.createRefreshToken(user.getEmail(), user.getRole());

            // Access Token을 헤더에 설정
            response.addHeader(JwtUtil.AUTHORIZATION_HEADER, JwtUtil.BEARER_PREFIX + accessToken);
            response.setHeader("Authorization", "Bearer " + accessToken);
            response.setHeader("Access-Control-Expose-Headers", "Authorization");

            // Refresh Token을 쿠키에 설정
            jwtUtil.setRefreshTokenCookie(response, refreshToken);

            // 프론트엔드 리다이렉트 URL 설정
            String targetUrl = createTargetUrl(accessToken);

            log.info("OAuth2 Login 성공 후 리다이렉트: {}", targetUrl);
            response.sendRedirect(targetUrl);
        }catch (Exception e){
            log.error("OAuth2 로그인 성공 처리 중 에러 발생", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

    /**
     * 프론트엔드 리다이렉트 URL 생성
     * 프론트엔드에서 토큰을 받을 수 있도록 쿼리 파라미터로 전달
     */
    private String createTargetUrl(String token) {
        return UriComponentsBuilder.fromUriString("http://localhost:3000/oauth/callback")
                .queryParam("token", token)
                .build()
                .toUriString();
    }
}
