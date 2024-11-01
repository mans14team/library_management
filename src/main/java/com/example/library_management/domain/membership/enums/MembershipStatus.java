package com.example.library_management.domain.membership.enums;

public enum MembershipStatus {
    ACTIVE("활성화"),
    EXPIRED("만료됨"),
    CANCELLED("취소됨");

    private final String description;

    MembershipStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
