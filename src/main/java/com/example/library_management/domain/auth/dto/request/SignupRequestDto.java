package com.example.library_management.domain.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.Email;
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
public class SignupRequestDto {

    @NotBlank
    @Email
    private String email;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[\\p{Punct}])[A-Za-z\\d\\p{Punct}]{8,20}$",
            message = "비밀번호는 최소 8자리 이상, 숫자, 영문, 특수문자를 1자 이상 포함되어야 합니다.")
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    @NotBlank
    private String userName;

    private boolean owner = false;
    private String ownerToken = "";
}
