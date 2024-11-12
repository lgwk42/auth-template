package com.project.authtemplate.domain.auth.client.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignInRequest(
        @NotBlank
        @Email
        String email,
        @NotBlank
        String password
){}