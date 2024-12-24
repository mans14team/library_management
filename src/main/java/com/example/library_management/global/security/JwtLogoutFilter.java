package com.example.library_management.global.security;

import com.example.library_management.domain.common.exception.GlobalException;
import com.example.library_management.domain.common.exception.GlobalExceptionConst;
import com.example.library_management.global.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j(topic = "로그아웃 처리")
public class JwtLogoutFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    public JwtLogoutFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // 로그아웃 요청이 아닌 경우 다음 필터로 진행
        if (!path.equals("/auth/logout") || !method.equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String accessToken = jwtUtil.getJwtFromHeader(request);
            String email = jwtUtil.getUserInfoFromToken(accessToken).getSubject();

            // AccessToken을 블랙리스트에 추가
            jwtUtil.addToBlacklist(accessToken);

            // Redis에서 RefreshToken 삭제
            jwtUtil.deleteRefreshToken(email);

            // RefreshToken 쿠키 제거
            Cookie cookie = new Cookie(JwtUtil.REFRESH_HEADER, null);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);

            // SecurityContext 초기화
            SecurityContextHolder.clearContext();

            log.info("로그아웃 성공: {}", email);

            // 다음 필터로 진행하여 Controller에서 응답 처리
            filterChain.doFilter(request, response);
        }catch (Exception e){
            throw new GlobalException(GlobalExceptionConst.UNAUTHORIZED_OWNERTOKEN);
        }
    }
}