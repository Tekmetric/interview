package com.interview.jwt;

import com.interview.testutil.CommonTestConstants;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;

    private final UserDetails userDetails = new User(
            CommonTestConstants.EMAIL_1,
            CommonTestConstants.PASSWORD,
            new ArrayList<>());
    private static final String SECRET_KEY = "a9b8c7d6e5f4g3h2i1j0k9l8m7n6o5p4q3r2s1t0u9v8w7x6y5z4a3b2c1d0";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET_KEY, CommonTestConstants.JWT_EXPIRES_IN);
    }

    @Test
    void testGenerateToken() {
        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertTrue(token.startsWith("eyJhbGciOiJIUzI1NiJ9"));
    }

    @Test
    void testExtractUsernameFromToken() {
        String token = jwtService.generateToken(userDetails);

        String extractedUsername = jwtService.extractUsername(token);

        assertEquals(CommonTestConstants.EMAIL_1, extractedUsername);
    }

    @Test
    void testValidToken() {
        String token = jwtService.generateToken(userDetails);

        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    void testInvalidToken() {
        String invalidToken = "invalid.jwt.token";

        assertThrows(io.jsonwebtoken.MalformedJwtException.class, () -> jwtService.extractUsername(invalidToken));
    }

    @Test
    void testExtractClaims() {
        String token = jwtService.generateToken(userDetails);

        String subject = jwtService.extractClaim(token, Claims::getSubject);

        assertEquals(CommonTestConstants.EMAIL_1, subject);
    }

    @Test
    void testReturningExpireTime() {
        long expirationTime = jwtService.getExpirationTime();

        assertEquals(CommonTestConstants.JWT_EXPIRES_IN, expirationTime);
    }

    @Test
    void testExpiredToken() throws InterruptedException {
        jwtService = new JwtService(SECRET_KEY, 1000);
        String token = jwtService.generateToken(userDetails);

        boolean isValid = jwtService.isTokenValid(token, userDetails);
        assertTrue(isValid);

        TimeUnit.SECONDS.sleep(3);
        assertThrows(io.jsonwebtoken.ExpiredJwtException.class,
                () -> jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void testNotExpiredToken() {
        String token = jwtService.generateToken(new User(
                CommonTestConstants.EMAIL_1,
                CommonTestConstants.PASSWORD,
                new ArrayList<>()));

        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    void testExtractingExpireFromToken() {
        String token = jwtService.generateToken(userDetails);

        Date expirationDate = jwtService.extractExpiration(token);

        assertNotNull(expirationDate);
        assertTrue(expirationDate.after(new Date()));
    }
}
