package com.example.library_management.domain.user.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NONE // 타입 정보를 사용하지 않음
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserChangePasswordRequestDto {
    @NotBlank
    private String oldPassword;
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[\\p{Punct}])[A-Za-z\\d\\p{Punct}]{8,20}$",
            message = "비밀번호는 최소 8자리 이상, 숫자, 영문, 특수문자를 1자 이상 포함되어야 합니다.")
    @NotBlank
    private String newPassword;
}
