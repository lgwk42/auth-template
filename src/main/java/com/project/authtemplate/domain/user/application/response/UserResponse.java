package com.project.authtemplate.domain.user.application.response;

import com.project.authtemplate.domain.user.domain.enums.UserRole;
import com.project.authtemplate.domain.user.domain.model.User;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserResponse(
        String email,
        String name,
        UserRole userRole,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {

    public static UserResponse of(User user) {
        return UserResponse.builder()
                .email(user.email())
                .name(user.name())
                .userRole(user.userRole())
                .createdAt(user.createdAt())
                .modifiedAt(user.modifiedAt())
                .build();
    }

}
