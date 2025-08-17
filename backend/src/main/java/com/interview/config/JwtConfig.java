package com.interview.config;

import io.jsonwebtoken.security.Keys;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

// Group related configs of JWT in a centralized file here.
@Configuration
@ConfigurationProperties(prefix = "jwt")
@Data
@Slf4j
public class JwtConfig {
    private String hmacKey;
    private int accessTokenExpiration;
    private int refreshTokenExpiration;

    public SecretKey getSecretKey() {
        log.debug("hmacKey value: {}", hmacKey);
        if (hmacKey == null) {
            log.error("hmacKey is null! Check application.properties");
        }
        return Keys.hmacShaKeyFor(hmacKey.getBytes());
    }
}