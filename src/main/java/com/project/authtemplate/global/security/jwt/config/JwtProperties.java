package com.project.authtemplate.global.security.jwt.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.jwt")
public record JwtProperties(
        String secretKey,
        long expiration,
        long refreshExpiration,
        String issuer
) {

}
