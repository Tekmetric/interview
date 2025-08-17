package com.interview.service;

import com.interview.config.JwtConfig;
import com.interview.dto.Jwt;
import com.interview.entity.Customer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@AllArgsConstructor
@Service
public class JwtService {
    private final JwtConfig jwtConfig;

    public Jwt generateAccessToken(Customer customer) {
        return generateToken(customer, jwtConfig.getAccessTokenExpiration());
    }

    public Jwt generateRefreshToken(Customer customer) {
        return generateToken(customer, jwtConfig.getRefreshTokenExpiration());
    }

    public Jwt generateToken(Customer customer, long tokenExpiration) {
        Claims claims = Jwts.claims()
                // sub: subject, unique identifier for the customer
                .subject(customer.getId().toString())
                .add("email", customer.getEmail())
                .add("firstname", customer.getFirstName())
                .add("lastname", customer.getLastName())
                .add("role", customer.getRole())
                // iat: issued at (epoch), time at which the token was issued
                .issuedAt(new Date())
                // exp: expiration time (epoch)
                .expiration(new Date(System.currentTimeMillis() + 1000 * tokenExpiration))
                .build();

        return new Jwt(claims, jwtConfig.getSecretKey());
    }

    // If parse failed, return null
    public Jwt parseToken(String token) {
        try {
            var claims = getClaims(token);
            return new Jwt(claims, jwtConfig.getSecretKey());
        } catch (JwtException e) {
            return null;
        }
    }

    // Verifies the token and return claims
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(jwtConfig.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
