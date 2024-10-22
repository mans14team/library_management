package com.example.library_management.domain.auth.service;

import com.example.library_management.domain.auth.dto.request.SigninRequestDto;
import com.example.library_management.domain.auth.dto.request.SignupRequestDto;
import com.example.library_management.domain.auth.exception.DeletedUserException;
import com.example.library_management.domain.auth.exception.DuplicateEmailException;
import com.example.library_management.domain.auth.exception.UnauthorizedPasswordException;
import com.example.library_management.domain.auth.exception.UserRoleException;
import com.example.library_management.domain.user.entity.User;
import com.example.library_management.domain.user.enums.UserRole;
import com.example.library_management.domain.user.enums.UserStatus;
import com.example.library_management.domain.user.exception.NotFoundUserException;
import com.example.library_management.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${owner.token}")
    private String OWNER_TOKEN;

    @Transactional
    public String signup(SignupRequestDto signupRequestDto) {

        if (userRepository.existsByEmail(signupRequestDto.getEmail())) {
            throw new DuplicateEmailException();
        }

        String encodedPassword = passwordEncoder.encode(signupRequestDto.getPassword());

        // 사용자 ROLE 확인
        UserRole role = UserRole.ROLE_USER;
        if (signupRequestDto.isOwner()) {
            if (!OWNER_TOKEN.equals(signupRequestDto.getOwnerToken())) {
                throw new UserRoleException();
            }
            role = UserRole.ROLE_ADMIN;
        }

        User newUser = new User(
                signupRequestDto.getEmail(),
                encodedPassword,
                signupRequestDto.getUserName(),
                role
        );

        User savedUser = userRepository.save(newUser);

        return "회원가입에 성공했습니다.";
    }

    public String signin(SigninRequestDto signinRequestDto) {
        User user = userRepository.findByEmail(signinRequestDto.getEmail()).orElseThrow(
                NotFoundUserException::new);

        // 로그인 시 이메일과 비밀번호가 일치하지 않을 경우 401을 반환
        if (!passwordEncoder.matches(signinRequestDto.getPassword(), user.getPassword())) {
            throw new UnauthorizedPasswordException();
        }

        // UserStatus 가 DELETED 면 로그인 불가능
        if (user.getStatus().equals(UserStatus.DELETED)) {
            throw new DeletedUserException();
        }

        return "로그인 성공했습니다.";
    }
}
