package com.project.authtemplate.domain.user.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {

    USER("ROLE_USER");

    private final String key;

}
