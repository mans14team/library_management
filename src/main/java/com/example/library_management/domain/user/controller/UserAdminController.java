package com.example.library_management.domain.user.controller;

import com.example.library_management.domain.user.dto.request.UserRoleChangeRequestDto;
import com.example.library_management.domain.user.enums.UserRole;
import com.example.library_management.domain.user.service.UserAdminService;
import com.example.library_management.global.config.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserAdminController {
    private final UserAdminService userAdminService;

    @Secured(UserRole.Authority.ADMIN)   // 관리자 권한만 접근 가능
    @PatchMapping("/admin/user/{userId}")
    public ResponseEntity<ApiResponse<String>> changeUserRole(@PathVariable long userId, @RequestBody UserRoleChangeRequestDto userRoleChangeRequestDto) {
        return ResponseEntity.ok(ApiResponse.success(userAdminService.changeUserRole(userId, userRoleChangeRequestDto)));
    }
}
