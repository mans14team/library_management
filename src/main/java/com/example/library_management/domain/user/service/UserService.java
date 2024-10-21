package com.example.library_management.domain.user.service;

import com.example.library_management.domain.auth.exception.InvalidPasswordFormatException;
import com.example.library_management.domain.auth.exception.UnauthorizedPasswordException;
import com.example.library_management.domain.user.dto.request.UserChangePasswordRequestDto;
import com.example.library_management.domain.user.dto.request.UserCheckPasswordRequestDto;
import com.example.library_management.domain.user.dto.response.UserResponseDto;
import com.example.library_management.domain.user.entity.User;
import com.example.library_management.domain.user.exception.DuplicatePasswordException;
import com.example.library_management.domain.user.repository.UserRepository;
import com.example.library_management.domain.user.exception.NotFoundUserException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDto getUser(long userId) {
        // 유저 조회
        User user = findUser(userId);
        return new UserResponseDto(user.getId(), user.getEmail());
    }

    @Transactional
    public String changePassword(User user, UserChangePasswordRequestDto userChangePasswordRequestDto) {
        validateNewPassword(userChangePasswordRequestDto);

        // 새 비밀번호 확인
        if (passwordEncoder.matches(userChangePasswordRequestDto.getNewPassword(), user.getPassword())) {
            throw new DuplicatePasswordException();
        }

        // 이전 비밀번호 확인
        if (!passwordEncoder.matches(userChangePasswordRequestDto.getOldPassword(), user.getPassword())) {
            throw new UnauthorizedPasswordException();
        }

        user.changePassword(passwordEncoder.encode(userChangePasswordRequestDto.getNewPassword()));

        userRepository.save(user);

        return "비밀번호가 정상적으로 변경되었습니다.";
    }
    @Transactional
    public String deleteUser(User user, UserCheckPasswordRequestDto userCheckPasswordRequestDto) {
        //비밀번호 확인
        if (!passwordEncoder.matches(userCheckPasswordRequestDto.getPassword(), user.getPassword())) {
            throw new UnauthorizedPasswordException();
        }

        // UserStatus DELETED 로 수정
        user.delete();

        userRepository.save(user);

        return "회원탈퇴가 정상적으로 완료되었습니다.";
    }

    // 패스워드 조건 확인 메서드
    private static void validateNewPassword(UserChangePasswordRequestDto userChangePasswordRequestDto) {
        if (userChangePasswordRequestDto.getNewPassword().length() < 8 ||
                !userChangePasswordRequestDto.getNewPassword().matches(".*\\d.*") ||
                !userChangePasswordRequestDto.getNewPassword().matches(".*[A-Z].*")) {
            throw new InvalidPasswordFormatException();
        }
    }

    // 유저 조회 메서드
    public User findUser(long userId) {
        return userRepository.findById(userId).orElseThrow(NotFoundUserException::new);
    }
}
