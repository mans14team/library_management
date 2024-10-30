package com.example.library_management.domain.membership.controller;

import com.example.library_management.domain.membership.dto.response.MembershipStatusResponse;
import com.example.library_management.domain.membership.service.MembershipService;
import com.example.library_management.global.config.ApiResponse;
import com.example.library_management.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/library/membership")
public class MembershipController {
    private final MembershipService membershipService;
    
    // 멤버십 상태 조회
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<MembershipStatusResponse>> getMembershipStatus(@AuthenticationPrincipal UserDetailsImpl userDetails){
        MembershipStatusResponse response = membershipService.getMembershipStatus(userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    //
}
