package com.interview.service;

import com.interview.config.JwtConfig;
import com.interview.entity.Customer;
import com.interview.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@Service
public class JwtService {
    private final JwtConfig jwtConfig;

    public String generateAccessToken(Customer customer) {
        return generateToken(customer, jwtConfig.getAccessTokenExpiration());
    }

    public String generateRefreshToken(Customer customer) {
        return generateToken(customer, jwtConfig.getRefreshTokenExpiration());
    }

    public String generateToken(Customer customer, long tokenExpiration) {

        return Jwts.builder()
                // sub: subject, unique identifier for the customer
                .subject(customer.getId().toString())
                .claim("email", customer.getEmail())
                .claim("firstname", customer.getFirstName())
                .claim("lastname", customer.getLastName())
                .claim("role", customer.getRole())
                // iat: issued at (epoch), time at which the token was issued
                .issuedAt(new Date())
                // exp: expiration time (epoch)
                .expiration(new Date(System.currentTimeMillis() + 1000 * tokenExpiration))
                // sign with HMAC key
                .signWith(jwtConfig.getSecretKey())
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = getClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (JwtException e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
            .verifyWith(jwtConfig.getSecretKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public UUID getCustomerIdFromToken(String token) {
        return UUID.fromString(getClaims(token).getSubject());
    }

    public Role getCustomerRoleFromToken(String token) {
        return Role.valueOf(getClaims(token).get("role", String.class));
    }
}
