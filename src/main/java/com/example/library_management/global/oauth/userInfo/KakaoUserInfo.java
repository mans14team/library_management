package com.example.library_management.global.oauth.userInfo;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class KakaoUserInfo extends OAuth2UserInfo {
    public KakaoUserInfo(Map<String, Object> attributes){
        super(attributes);
    }

    @Override
    public String getId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getEmail() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        if(kakaoAccount == null) {
            return null;
        }
        return (String) kakaoAccount.get("email");
    }

    @Override
    public String getNickname() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        if(kakaoAccount == null) {
            return null;
        }

        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        if(profile == null) {
            return null;
        }
        return (String) profile.get("nickname");
    }

    // 카카오 사용자 정보 디버깅을 위한 메소드
    public void printAttributes() {
        log.info("Kakao OAuth2 User Attributes: {}", attributes);
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        if(kakaoAccount != null) {
            log.info("Kakao Account: {}", kakaoAccount);
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            if(profile != null) {
                log.info("Profile: {}", profile);
            }
        }
    }
}
