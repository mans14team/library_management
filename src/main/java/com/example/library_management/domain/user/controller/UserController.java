package com.example.library_management.domain.user.controller;

import com.example.library_management.domain.user.dto.request.UserChangePasswordRequestDto;
import com.example.library_management.domain.user.dto.request.UserCheckPasswordRequestDto;
import com.example.library_management.domain.user.dto.response.UserResponseDto;
import com.example.library_management.domain.user.service.UserService;
import com.example.library_management.global.config.ApiResponse;
import com.example.library_management.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 유저 조회 ( id )
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUser(@PathVariable long userId) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUser(userId)));
    }

    // 유저 비밀번호 변경
    @PutMapping("/user")
    public ResponseEntity<ApiResponse<String>> changePassword(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody UserChangePasswordRequestDto userChangePasswordRequestDto) {
        return ResponseEntity.ok(ApiResponse.success(userService.changePassword(userDetails.getUser(), userChangePasswordRequestDto)));
    }

    // 유저 회원탈퇴
    @DeleteMapping("/user")
    public ResponseEntity<ApiResponse<String>> deleteUser(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody UserCheckPasswordRequestDto userCheckPasswordRequestDto) {
        return ResponseEntity.ok(ApiResponse.success(userService.deleteUser(userDetails.getUser(), userCheckPasswordRequestDto)));
    }
}
