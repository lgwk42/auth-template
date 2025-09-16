package com.project.authtemplate.domain.user.client.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UserEditRequest(
        @NotBlank(message = "이름을 필수입니다.")
        String name
) {}
