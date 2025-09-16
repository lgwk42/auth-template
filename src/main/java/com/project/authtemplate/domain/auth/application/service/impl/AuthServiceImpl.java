package com.project.authtemplate.domain.auth.application.service.impl;

import com.project.authtemplate.domain.auth.application.response.JsonWebTokenResponse;
import com.project.authtemplate.domain.auth.application.response.RefreshTokenResponse;
import com.project.authtemplate.domain.auth.application.service.AuthService;
import com.project.authtemplate.domain.user.domain.enums.UserRole;
import com.project.authtemplate.domain.user.exception.PasswordWrongException;
import com.project.authtemplate.global.security.jwt.JwtProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    public JsonWebTokenResponse generateToken(String email, UserRole role) {
        return JsonWebTokenResponse.builder()
                .accessToken(jwtProvider.generateAccessToken(email, role))
                .refreshToken(jwtProvider.generateRefreshToken(email, role))
                .build();
    }

    @Override
    public RefreshTokenResponse refreshToken(String refreshToken) {
        Jws<Claims> claims = jwtProvider.getClaims(refreshToken);
        return RefreshTokenResponse.builder()
                .accessToken(jwtProvider.generateAccessToken(claims.getBody().getSubject(),
                        (UserRole) claims.getHeader().get("authority"))).build();
    }

    @Override
    public void checkPassword(String rawPassword, String hashedPassword) {
        if (!passwordEncoder.matches(rawPassword, hashedPassword)) {
            throw PasswordWrongException.EXCEPTION;
        }
    }

}