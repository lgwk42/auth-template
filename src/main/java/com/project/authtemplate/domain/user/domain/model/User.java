package com.project.authtemplate.domain.user.domain.model;

import com.project.authtemplate.domain.user.domain.entity.UserEntity;
import com.project.authtemplate.domain.user.domain.enums.UserRole;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record User(
        String email,
        String password,
        String name,
        UserRole userRole,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {

    public static User of(UserEntity entity) {
        return User.builder()
                .email(entity.getEmail())
                .password(entity.getPassword())
                .name(entity.getName())
                .userRole(entity.getUserRole())
                .createdAt(entity.getCreatedAt())
                .modifiedAt(entity.getModifiedAt())
                .build();
    }

}
