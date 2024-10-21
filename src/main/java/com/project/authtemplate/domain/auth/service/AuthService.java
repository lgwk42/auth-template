package com.project.authtemplate.domain.auth.service;

import com.project.authtemplate.domain.auth.client.dto.request.SignInRequest;
import com.project.authtemplate.domain.auth.client.dto.request.SignUpRequest;
import com.project.authtemplate.domain.auth.service.response.JsonWebTokenResponse;
import com.project.authtemplate.domain.auth.service.response.RefreshTokenResponse;
import com.project.authtemplate.domain.user.domain.entity.UserEntity;
import com.project.authtemplate.domain.user.domain.enums.UserRole;
import com.project.authtemplate.domain.user.domain.repository.jpa.UserJpaRepository;
import com.project.authtemplate.domain.user.exception.PasswordWrongException;
import com.project.authtemplate.domain.user.exception.UserExistException;
import com.project.authtemplate.domain.user.exception.UserNotFoundException;
import com.project.authtemplate.global.security.jwt.JwtExtract;
import com.project.authtemplate.global.security.jwt.JwtProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder encoder;
    private final JwtProvider jwtProvider;
    private final JwtExtract jwtExtract;

    public void signUp(SignUpRequest request) {
        if (checkUserByEmail(request.email())){
            throw UserExistException.EXCEPTION;
        }
        userJpaRepository.save(UserEntity.builder()
                .email(request.email())
                .name(request.name())
                .password(encoder.encode(request.password()))
                .userRole(UserRole.USER)
                .build()
        );
    }

    public JsonWebTokenResponse signIn(SignInRequest request) {
        if(!checkUserByEmail(request.email())){
            throw UserNotFoundException.EXCEPTION;
        }
        String userPassword = userJpaRepository.getByEmail(request.email()).getPassword();
        if (!encoder.matches(request.password(), userPassword))
            throw PasswordWrongException.EXCEPTION;
        return JsonWebTokenResponse.builder()
                .accessToken(jwtProvider.generateAccessToken(request.email(), UserRole.USER))
                .refreshToken(jwtProvider.generateRefreshToken(request.email(), UserRole.USER))
                .build();
    }

    public RefreshTokenResponse refresh(String token) {
        Jws<Claims> claims = jwtProvider.getClaims(token);
        return RefreshTokenResponse.builder()
                .accessToken(jwtProvider.generateAccessToken(jwtExtract.getEmail(token),
                        (UserRole) claims.getHeader().get("authority"))).build();
    }

    public boolean checkUserByEmail(String email) {
        // 유저가 존재한다면 true, 없다면 false
        return userJpaRepository.findByEmail(email).isPresent();
    }

}
