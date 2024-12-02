package com.example.library_management.global.oauth.enums;

public enum SocialType {
    KAKAO("kakao"),
    NAVER("naver");

    private final String registrationId;

    SocialType(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public static SocialType from(String registrationId) {
        for(SocialType socialType : values()) {
            if(socialType.getRegistrationId().equals(registrationId)) {
                return socialType;
            }
        }
        return null;
    }
}
