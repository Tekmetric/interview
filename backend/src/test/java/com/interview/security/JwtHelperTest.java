package com.interview.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JwtHelperTest {

    private static final String SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private static final long EXPIRATION_MS = 86400000L;

    private JwtHelper jwtHelper;

    @BeforeEach
    void setUp() {
        jwtHelper = new JwtHelper(SECRET, EXPIRATION_MS);
    }

    @Test
    void givenUsername_whenGenerateToken_thenTokenIsNotBlank() {
        String token = jwtHelper.generateToken("admin");

        assertThat(token).isNotBlank();
    }

    @Test
    void givenValidToken_whenExtractUsername_thenReturnsCorrectUsername() {
        String token = jwtHelper.generateToken("employee");

        assertThat(jwtHelper.extractUsername(token)).isEqualTo("employee");
    }

    @Test
    void givenValidToken_whenIsTokenValid_thenReturnsTrue() {
        String token = jwtHelper.generateToken("admin");

        assertThat(jwtHelper.isTokenValid(token)).isTrue();
    }

    @Test
    void givenExpiredToken_whenIsTokenValid_thenReturnsFalse() {
        String expiredToken = Jwts.builder()
                .subject("admin")
                .issuedAt(new Date(System.currentTimeMillis() - 10000))
                .expiration(new Date(System.currentTimeMillis() - 5000))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET)))
                .compact();

        assertThat(jwtHelper.isTokenValid(expiredToken)).isFalse();
    }

    @Test
    void givenMalformedToken_whenIsTokenValid_thenReturnsFalse() {
        assertThat(jwtHelper.isTokenValid("not.a.valid.token")).isFalse();
    }
}
