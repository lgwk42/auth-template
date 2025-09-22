package com.project.authtemplate.global.security.jwt;

import com.project.authtemplate.domain.user.domain.enums.UserRole;
import com.project.authtemplate.global.security.jwt.config.JwtProperties;
import com.project.authtemplate.global.security.jwt.enums.JwtType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
    }

    public Jws<Claims> getClaims(final String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
        } catch (ExpiredJwtException e) {
            throw new IllegalArgumentException("만료된 토큰", e);
        } catch (SecurityException | SignatureException e) {
            throw new IllegalArgumentException("서명 검증 실패", e);
        } catch (MalformedJwtException e) {
            throw new IllegalArgumentException("손상된 토큰", e);
        } catch (UnsupportedJwtException e) {
            throw new IllegalArgumentException("지원되지 않는 토큰", e);
        } catch (JwtException | IllegalArgumentException e) {
            throw new IllegalArgumentException("잘못된 토큰", e);
        }
    }

    public String generateAccessToken(final String email, final UserRole userRole) {
        Instant now = Instant.now();
        return Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .subject(email)
                .claim("token_type", JwtType.ACCESS.name())
                .claim("authority", userRole.getKey())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(jwtProperties.getExpiration(), ChronoUnit.MILLIS)))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    public String generateRefreshToken(final String email, final UserRole userRole) {
        Instant now = Instant.now();
        return Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .subject(email)
                .claim("token_type", JwtType.REFRESH.name())
                .claim("authority", userRole.getKey())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(jwtProperties.getRefreshExpiration(), ChronoUnit.MILLIS)))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

}