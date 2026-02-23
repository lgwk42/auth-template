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
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.secretKey().getBytes(StandardCharsets.UTF_8));
    }

    public Jws<Claims> getClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .requireIssuer(jwtProperties.issuer())
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

    public String generateAccessToken(String email, UserRole userRole) {
        return Jwts.builder()
                .header().type("JWT").and()
                .subject(email)
                .issuer(jwtProperties.issuer())
                .claim("token_type", JwtType.ACCESS.name())
                .claim("authority", userRole.getKey())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.expiration()))
                .signWith(getSigningKey())
                .compact();
    }

    /** Refresh 토큰으로부터 Access 토큰 재발급 (authority 없이) */
    public String generateAccessTokenFromRefresh(String email) {
        return Jwts.builder()
                .header().type("JWT").and()
                .subject(email)
                .issuer(jwtProperties.issuer())
                .claim("token_type", JwtType.ACCESS.name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.expiration()))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .header().type("JWT").and()
                .subject(email)
                .issuer(jwtProperties.issuer())
                .claim("token_type", JwtType.REFRESH.name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.refreshExpiration()))
                .signWith(getSigningKey())
                .compact();
    }

}
