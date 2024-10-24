package com.example.library_management.global.jwt;

import com.example.library_management.domain.book.controller.BookController;
import com.example.library_management.domain.user.enums.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j(topic = "JwtUtil")
@Component
@RequiredArgsConstructor
public class JwtUtil {
    // Header KEY 값
    public static final String AUTHORIZATION_HEADER = "Authorization";
    // Refresh KEY 값
    public static final String REFRESH_HEADER = "Refresh-Token";
    // 사용자 권한 값의 KEY
    public static final String AUTHORIZATION_KEY = "auth";
    // Token 식별자
    public static final String BEARER_PREFIX = "Bearer ";
    // Access 토큰 만료시간
    private final long ACCESS_TOKEN_TIME = 30 * 60 * 1000L; // 60분
    // refresh 토큰 만료시간
    private final long REFRESH_TOKEN_TIME = 7 * 24 * 60 * 60 * 1000L; // 7일

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);


    @Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    private final RedisTemplate<String, String> redisTemplate;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // Access 토큰 생성
    public String createAccessToken(String email, UserRole role) {
        return createToken(email, role, ACCESS_TOKEN_TIME);
    }

    // RefreshToken 생성 메소드 수정
    public String createRefreshToken(String email, UserRole role) {
        String token = createToken(email, role, REFRESH_TOKEN_TIME);
        // Redis에 RefreshToken 저장
        redisTemplate.opsForValue().set(
                email,
                token,
                REFRESH_TOKEN_TIME,
                TimeUnit.MILLISECONDS
        );
        log.info("RefreshToken created and saved in Redis for user: {}", email);
        return token;
    }

    // RefreshToken 쿠키에 설정
    public void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie(REFRESH_HEADER, refreshToken);
        cookie.setPath("/");
        cookie.setMaxAge((int) (REFRESH_TOKEN_TIME / 1000));
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    // Refresh 토큰 쿠키에서 가져오기
    public Optional<String> getRefreshTokenFromCooke(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (REFRESH_HEADER.equals(cookie.getName())) {
                    return Optional.of(cookie.getValue());
                }
            }
        }
        return Optional.empty();
    }

    // RefreshToken 업데이트 메소드 수정
    public String updateRefreshToken(String email, UserRole role) {
        String newToken = createToken(email, role, REFRESH_TOKEN_TIME);
        // Redis에서 기존 토큰 삭제 후 새 토큰 저장
        redisTemplate.delete(email);
        redisTemplate.opsForValue().set(
                email,
                newToken,
                REFRESH_TOKEN_TIME,
                TimeUnit.MILLISECONDS
        );
        return newToken;
    }

    // RefreshToken 삭제 메소드 수정
    public void deleteRefreshToken(String userEmail) {
        redisTemplate.delete(userEmail);
    }

    // 토큰 생성
    public String createToken(String email, UserRole role, long tokenTime) {
        Date date = new Date();

        return Jwts.builder()
                .setSubject(email) // 사용자 식별자값(ID)
                .claim(AUTHORIZATION_KEY, role) // 사용자 권한
                .setExpiration(new Date(date.getTime() + tokenTime)) // 만료 시간
                .setIssuedAt(date) // 발급일
                .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                .compact();
    }

    // header 에서 JWT 가져오기
    public String getJwtFromHeader(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) {
            token = token.substring(7);
            while (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) {
                token = token.substring(7);
            }
            return token;
        }
        throw new NullPointerException("Not Found Token");
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        log.info("Validating JWT token");
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            log.info("Token is valid");
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.", e);
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token, 만료된 JWT token 입니다.", e);
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.", e);
        } catch (IllegalArgumentException e) {
            log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.", e);
        }catch (Exception e) {
            log.error("Internal server error", e);
        }
        return false;
    }

    // Refresh Token 검증 (Access Token 검증과 동일한 로직 사용)
    public boolean validateRefreshToken(String token) {
        return validateToken(token);
    }

    // 토큰에서 사용자 정보 가져오기
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    // Redis에서 RefreshToken 조회 메소드 추가
    public Optional<String> getRefreshTokenFromRedis(String email) {
        String token = redisTemplate.opsForValue().get(email);
        return Optional.ofNullable(token);
    }
}
