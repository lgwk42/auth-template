package com.project.authtemplate.domain.auth.client.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record SignUpRequest(

        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "유효한 이메일 주소를 입력해주세요.")
        String email,
        @NotBlank(message = "이름은 필수입니다.")
        String name,
        @NotBlank(message = "비밀번호는 필수입니다.")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[!@#$&_\\-])[A-Za-z0-9!@#$&_\\-]{10,}$",
                message = "비밀번호는 영문 대문자, 소문자, 특수문자를 포함하고 10자 이상이어야 합니다."
        )
        String password

) {}