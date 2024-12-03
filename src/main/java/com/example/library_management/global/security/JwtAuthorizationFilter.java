package com.example.library_management.global.security;

import com.example.library_management.domain.user.enums.UserRole;
import com.example.library_management.global.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Slf4j(topic = "JWT 검증 및 인가")
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthorizationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        String path = req.getRequestURI();
        // OAuth2 관련 경로 및 로그인, 회원가입 요청은 이 필터를 건너뜁니다.
        if (path.startsWith("/oauth2") || path.startsWith("/library/books/search") || path.equals("/auth/signup") || path.equals("/auth/signin")
                || path.equals("/actuator/health") || path.equals("/") || path.equals("/error") || path.equals("/favicon.ico") || path.equals("/library/books/autocomplete")){
            filterChain.doFilter(req, res);
            return;
        }

        String accessToken = jwtUtil.getJwtFromHeader(req);
        log.info("Extracted accessToken from header: {}", accessToken);
        Optional<String> refreshTokenOpt = jwtUtil.getRefreshTokenFromCooke(req);

        if (StringUtils.hasText(accessToken)) {
            if (jwtUtil.validateToken(accessToken)) {
                setAuthentication(jwtUtil.getUserInfoFromToken(accessToken).getSubject());
            } else if (refreshTokenOpt.isPresent() && jwtUtil.validateRefreshToken(refreshTokenOpt.get())) {
                // AccessToken이 만료되었지만 RefreshToken이 유효한 경우
                String refreshToken = refreshTokenOpt.get();
                String email = jwtUtil.getUserInfoFromToken(refreshToken).getSubject();

                // Redis에서 RefreshToken 조회
                Optional<String> storedRefreshToken = jwtUtil.getRefreshTokenFromRedis(email);

                if (storedRefreshToken.isPresent() && storedRefreshToken.get().equals(refreshToken)) {
                    UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(email);
                    UserRole role = userDetails.getUser().getRole();

                    String newAccessToken = jwtUtil.createAccessToken(email, role);
                    res.addHeader(JwtUtil.AUTHORIZATION_HEADER, JwtUtil.BEARER_PREFIX + newAccessToken);

                    String newRefreshToken = jwtUtil.updateRefreshToken(email, role);
                    jwtUtil.setRefreshTokenCookie(res, newRefreshToken);

                    setAuthentication(email);
                }
            }

            filterChain.doFilter(req, res);
        }
    }

    // 인증 처리
    public void setAuthentication(String email) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(email);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String email) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
