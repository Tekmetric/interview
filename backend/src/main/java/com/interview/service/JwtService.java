package com.interview.service;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {
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
                // sign with hmac key
                .signWith(Keys.hmacShaKeyFor("this-is-the-hmac-key-retrived-from-vault".getBytes()))
                .compact();
    }
}
