package com.project.authtemplate.domain.user.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {

    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN"),
    MASTER("ROLE_MASTER");

    private final String key;

}
