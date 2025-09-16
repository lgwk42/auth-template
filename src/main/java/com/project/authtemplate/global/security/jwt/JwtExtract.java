package com.project.authtemplate.global.security.jwt;

import com.project.authtemplate.domain.user.client.dto.User;
import com.project.authtemplate.domain.user.domain.repository.jpa.UserJpaRepository;
import com.project.authtemplate.domain.user.exception.UserNotFoundException;
import com.project.authtemplate.global.security.auth.CustomUserDetails;
import com.project.authtemplate.global.security.jwt.enums.JwtType;
import com.project.authtemplate.global.security.jwt.exception.TokenTypeException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class JwtExtract {

    private final UserJpaRepository userRepository;
    private final JwtProvider jwtProvider;

    public Authentication getAuthentication(final String token) {
        final Jws<Claims> jws = jwtProvider.getClaims(token);
        final Claims claims = jws.getPayload();
        if (isWrongType(claims, JwtType.ACCESS)) {
            throw TokenTypeException.EXCEPTION;
        }
        User user = userRepository
                .findByEmail(claims.getSubject())
                .map(User::toUser)
                .orElseThrow(() -> UserNotFoundException.EXCEPTION);
        final CustomUserDetails details = new CustomUserDetails(user);
        return new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
    }

    public String extractTokenFromRequest(HttpServletRequest request) {
        return extractToken(request.getHeader(HttpHeaders.AUTHORIZATION));
    }

    public String extractToken(final String token) {
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return token;
    }

    public boolean isWrongType(final Claims claims, final JwtType jwtType) {
        return !(claims.get("token_type").equals(jwtType.toString()));
    }

}