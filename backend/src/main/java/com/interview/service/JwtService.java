package com.interview.service;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {
    @Value("${jwt.hamc.key}")
    private String jwtHmacKey;

    public String generateToken(String email) {
        // TODO put in config, 1 day
        final long tokenExpiration = 86400;

        return Jwts.builder()
                // sub
                .subject(email)
                // iat
                .issuedAt(new Date())
                // exp
                .expiration(new Date(System.currentTimeMillis() + 1000 * tokenExpiration))
                // TODO put in config
                // sign with HMAC key
                .signWith(Keys.hmacShaKeyFor(jwtHmacKey.getBytes()))
                .compact();
    }
}
