package com.project.authtemplate.domain.auth.client.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "토큰은 필수입니다.")
        String refreshToken
){}