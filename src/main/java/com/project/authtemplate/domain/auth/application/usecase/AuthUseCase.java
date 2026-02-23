package com.project.authtemplate.domain.auth.application.usecase;

import com.project.authtemplate.domain.auth.application.response.JsonWebTokenResponse;
import com.project.authtemplate.domain.auth.application.response.RefreshTokenResponse;
import com.project.authtemplate.domain.auth.application.service.AuthService;
import com.project.authtemplate.domain.auth.client.request.RefreshTokenRequest;
import com.project.authtemplate.domain.auth.client.request.SignInRequest;
import com.project.authtemplate.domain.auth.client.request.SignUpRequest;
import com.project.authtemplate.domain.user.application.service.UserService;
import com.project.authtemplate.domain.user.domain.enums.UserRole;
import com.project.authtemplate.domain.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthUseCase {

    private final AuthService authService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public void signUp(SignUpRequest request) {
        log.info("[AuthUseCase] signUp - email={}", request.email());
        userService.validateEmailNotExist(request.email());
        userService.save(
                request.email(),
                passwordEncoder.encode(request.password()),
                request.name(),
                UserRole.USER
        );
    }

    public JsonWebTokenResponse signIn(SignInRequest request) {
        log.info("[AuthUseCase] signIn - email={}", request.email());
        User user = userService.findByEmail(request.email());
        authService.checkPassword(request.password(), user.password());
        return authService.generateToken(request.email(), user.userRole());
    }

    public RefreshTokenResponse refresh(RefreshTokenRequest request) {
        log.info("[AuthUseCase] refresh");
        return authService.refreshToken(request.refreshToken());
    }

}
