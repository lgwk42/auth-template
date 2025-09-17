package com.project.authtemplate.domain.auth.application.usecase;

import com.project.authtemplate.domain.auth.application.response.JsonWebTokenResponse;
import com.project.authtemplate.domain.auth.application.response.RefreshTokenResponse;
import com.project.authtemplate.domain.auth.application.service.AuthService;
import com.project.authtemplate.domain.auth.client.request.RefreshTokenRequest;
import com.project.authtemplate.domain.auth.client.request.SignInRequest;
import com.project.authtemplate.domain.auth.client.request.SignUpRequest;
import com.project.authtemplate.domain.user.client.dto.User;
import com.project.authtemplate.domain.user.domain.entity.UserEntity;
import com.project.authtemplate.domain.user.domain.enums.UserRole;
import com.project.authtemplate.domain.user.application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthUseCase {

    private final AuthService authService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public void signUp(SignUpRequest request) {
        userService.checkUserEmail(request.email());
        userService.save(UserEntity.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .name(request.name())
                .userRole(UserRole.USER)
                .build()
        );
    }

    public JsonWebTokenResponse signIn(SignInRequest request) {
        User user = userService.getUser(request.email());
        authService.checkPassword(request.password(), user.password());
        return authService.generateToken(request.email(), user.userRole());
    }

    public RefreshTokenResponse refresh(RefreshTokenRequest request) {
        return authService.refreshToken(request.refreshToken());
    }

}
