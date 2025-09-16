package com.project.authtemplate.domain.auth.application.service;

import com.project.authtemplate.domain.auth.application.response.JsonWebTokenResponse;
import com.project.authtemplate.domain.auth.application.response.RefreshTokenResponse;
import com.project.authtemplate.domain.user.domain.enums.UserRole;

public interface AuthService {

    JsonWebTokenResponse generateToken(String email, UserRole role);

    RefreshTokenResponse refreshToken(String refreshToken);

    void checkPassword(String rawPassword, String hashedPassword);

}