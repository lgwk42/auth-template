package com.project.authtemplate.domain.auth.client.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


public record SignInRequest(

        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "유효한 이메일 주소를 입력해주세요.")
        String email,
        @NotBlank(message = "비밀번호는 필수입니다.")
        String password

) {}
