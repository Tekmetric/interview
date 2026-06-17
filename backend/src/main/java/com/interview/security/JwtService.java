package com.interview.security;

import com.interview.dto.AuthTokenResponse;
import com.interview.entity.AppUser;
import com.interview.entity.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey signingKey;
    private final Duration accessTokenTtl;

    public JwtService(
            @Value("${app.security.jwt.secret}") String secret,
            @Value("${app.security.jwt.access-token-ttl}") Duration accessTokenTtl
    ) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenTtl = accessTokenTtl;
    }

    public AuthTokenResponse createAccessToken(AppUser user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(accessTokenTtl);
        String token = Jwts.builder()
                .subject(user.getEmail())
                .claim("uid", user.getId())
                .claim("role", user.getRole().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(signingKey)
                .compact();

        return new AuthTokenResponse(token, "Bearer", accessTokenTtl.toSeconds(), user.getRole());
    }

    public AuthenticatedUser parseAuthenticatedUser(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return new AuthenticatedUser(
                claims.get("uid", Long.class),
                claims.getSubject(),
                UserRole.valueOf(claims.get("role", String.class))
        );
    }
}
