package com.project.authtemplate.global.security.jwt;

import com.project.authtemplate.domain.user.domain.repository.jpa.UserJpaRepository;
import com.project.authtemplate.domain.user.dto.User;
import com.project.authtemplate.domain.user.exception.UserNotFoundException;
import com.project.authtemplate.global.security.auth.CustomUserDetails;
import com.project.authtemplate.global.security.jwt.config.JwtProperties;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class JwtExtract {

    private final UserJpaRepository userRepository;
    private final User userDTO;
    private final JwtProperties jwtProperties;

    private final SecretKey secretKey = new SecretKeySpec(
            this.jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8),
            Jwts.SIG.HS256.key().build().getAlgorithm()
    );

    public Authentication getAuthentication(final String token) {
        User user = userRepository
                .findByEmail(getEmail(token))
                .map(userDTO::toUser)
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

    public String getEmail(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token).getPayload().get(
                        "email", String.class);
    }

}
