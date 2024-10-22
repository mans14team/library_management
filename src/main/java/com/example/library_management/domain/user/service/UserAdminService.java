package com.example.library_management.domain.user.service;

import com.example.library_management.domain.auth.exception.UserRoleException;
import com.example.library_management.domain.user.dto.request.UserRoleChangeRequestDto;
import com.example.library_management.domain.user.entity.User;
import com.example.library_management.domain.user.enums.UserRole;
import com.example.library_management.domain.user.repository.UserRepository;
import com.example.library_management.domain.user.exception.NotFoundUserException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserAdminService {
    private final UserRepository userRepository;

    @Value("${owner.token}")
    private String OWNER_TOKEN;

    @Transactional
    public String changeUserRole(long userId, UserRoleChangeRequestDto userRoleChangeRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(NotFoundUserException::new);

        UserRole newRole;
        if (userRoleChangeRequestDto.isOwner()) {
            if (!OWNER_TOKEN.equals(userRoleChangeRequestDto.getOwnerToken())) {
                throw new UserRoleException();
            }
            newRole = UserRole.ROLE_ADMIN;
        } else {
            newRole = UserRole.ROLE_USER;
        }

        // 현재 역할과 새로운 역할이 다른 경우에만 변경
        if (!user.getRole().equals(newRole)) {
            user.setRole(newRole);
            userRepository.save(user);
            return "User role changed to " + newRole.getAuthority();
        } else {
            return "User already has the role " + newRole.getAuthority();
        }
    }
}
