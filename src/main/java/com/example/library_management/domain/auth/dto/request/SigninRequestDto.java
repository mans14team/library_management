package com.example.library_management.domain.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NONE // 타입 정보를 사용하지 않음
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SigninRequestDto {

    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String password;
}
