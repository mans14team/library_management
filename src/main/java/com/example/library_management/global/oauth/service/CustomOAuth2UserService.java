package com.example.library_management.global.oauth.service;

import com.example.library_management.domain.user.entity.User;
import com.example.library_management.domain.user.enums.UserRole;
import com.example.library_management.domain.user.repository.UserRepository;
import com.example.library_management.global.oauth.enums.SocialType;
import com.example.library_management.global.oauth.userInfo.KakaoUserInfo;
import com.example.library_management.global.oauth.userInfo.NaverUserInfo;
import com.example.library_management.global.oauth.userInfo.OAuth2UserInfo;
import com.example.library_management.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 기본 OAuth2User 객체 생성
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 소셜 로그인 타입 (카카오) 확인
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        SocialType socialType = SocialType.from(registrationId);

        // 사용자 정보 추출을 위한 OAuth2UserInfo 객체 생성
        OAuth2UserInfo userInfo = null;
        if (socialType == SocialType.KAKAO){
            log.info("Kakao OAuth2 User Attributes: {}", oAuth2User.getAttributes());
            userInfo = new KakaoUserInfo(oAuth2User.getAttributes());
        }else if (socialType == SocialType.NAVER) {  // 추가
            log.info("Naver OAuth2 User Attributes: {}", oAuth2User.getAttributes());
            userInfo = new NaverUserInfo(oAuth2User.getAttributes());
        }

        // 필수 정보 추출
        String email = userInfo.getEmail();
        String nickname = userInfo.getNickname();
        String socialId = userInfo.getId();

        // 먼저 소셜 ID로 사용자 찾기
        Optional<User> userBySocialId = userRepository.findBySocialTypeAndSocialId(socialType, socialId);
        if (userBySocialId.isPresent()) {
            return new UserDetailsImpl(userBySocialId.get(), oAuth2User.getAttributes());
        }

        // 이메일로 기존 회원 찾기
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            // 기존 회원에게 소셜 정보 연동
            user.connectSocialAccount(socialType, socialId);
            userRepository.save(user);
            log.info("Existing user connected with social account: {}", email);
            return new UserDetailsImpl(user, oAuth2User.getAttributes());
        }

        // 신규 회원 생성
        User newUser = createUser(email, nickname, socialType, socialId);
        log.info("New OAuth2 user created: {}", email);
        return new UserDetailsImpl(newUser, oAuth2User.getAttributes());
    }

    // 새로운 회원 생성 메서드
    private User createUser(String email, String nickname, SocialType socialType, String socialId) {
        String password = UUID.randomUUID().toString();
        String encodedPassword = passwordEncoder.encode(password);

        User user = new User(
                email,
                encodedPassword,
                nickname,
                UserRole.ROLE_USER,
                socialType,
                socialId
        );

        return userRepository.save(user);
    }
}
