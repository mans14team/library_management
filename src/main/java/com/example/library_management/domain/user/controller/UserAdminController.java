package com.example.library_management.domain.user.controller;

import com.example.library_management.domain.user.dto.request.UserRoleChangeRequestDto;
import com.example.library_management.domain.user.service.UserAdminService;
import com.example.library_management.global.config.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/library")
public class UserAdminController {
    private final UserAdminService userAdminService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/admin/user/{userId}")
    public ResponseEntity<ApiResponse<String>> changeUserRole(@PathVariable long userId, @RequestBody UserRoleChangeRequestDto userRoleChangeRequestDto) {
        return ResponseEntity.ok(ApiResponse.success(userAdminService.changeUserRole(userId, userRoleChangeRequestDto)));
    }
}
