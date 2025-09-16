package com.project.authtemplate.domain.user.client.dto.request;

public record UserEditIsActiveRequest(
        String email,
        boolean isActive
) {
}
