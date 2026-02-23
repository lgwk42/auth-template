package com.project.authtemplate.domain.auth.application.service;

import com.project.authtemplate.domain.auth.application.response.JsonWebTokenResponse;
import com.project.authtemplate.domain.auth.application.response.RefreshTokenResponse;
import com.project.authtemplate.domain.user.domain.enums.UserRole;
import com.project.authtemplate.domain.user.exception.PasswordWrongException;
import com.project.authtemplate.global.security.jwt.JwtProvider;
import com.project.authtemplate.global.security.jwt.enums.JwtType;
import com.project.authtemplate.global.security.jwt.exception.TokenTypeException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    public JsonWebTokenResponse generateToken(String email, UserRole role) {
        return JsonWebTokenResponse.builder()
                .accessToken(jwtProvider.generateAccessToken(email, role))
                .refreshToken(jwtProvider.generateRefreshToken(email))
                .build();
    }

    public RefreshTokenResponse refreshToken(String refreshToken) {
        Jws<Claims> jws = jwtProvider.getClaims(refreshToken);
        Claims claims = jws.getPayload();
        if (!claims.get("token_type").equals(JwtType.REFRESH.name())) {
            throw TokenTypeException.EXCEPTION;
        }
        String email = claims.getSubject();
        return RefreshTokenResponse.builder()
                .accessToken(jwtProvider.generateAccessTokenFromRefresh(email))
                .build();
    }

    public void checkPassword(String rawPassword, String hashedPassword) {
        if (!passwordEncoder.matches(rawPassword, hashedPassword)) {
            throw PasswordWrongException.EXCEPTION;
        }
    }

}
