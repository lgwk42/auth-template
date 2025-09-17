package com.project.authtemplate.domain.user.application.response;

import com.project.authtemplate.domain.user.client.dto.User;
import com.project.authtemplate.domain.user.domain.enums.UserRole;

import java.time.LocalDateTime;

public record UserResponse(
        String email,
        String name,
        UserRole userRole,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {
    public static UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.email(),
                user.name(),
                user.userRole(),
                user.createdAt(),
                user.modifiedAt()
        );
    }
}
