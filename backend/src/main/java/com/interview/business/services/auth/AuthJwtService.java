package com.interview.business.services.auth;

import com.interview.business.domain.AppUser;
import com.interview.business.services.auth.dto.AuthUserToken;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

@Component
public class AuthJwtService {
    private final SecretKey SIGN_KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode("9a4f2c8d3b7a1e6f45c8a0b3f267d8b1d4e6f3c8a9d2b5f8e3a9c8b5f6v8a3d9"));

    public String createBy(AuthUserToken token) {
        return Jwts.builder()
                .subject("Authentication")
                .issuedAt(new Date())
                .issuer("Issuer")
                .signWith(SIGN_KEY)
                .claim("id", token.getId())
                .claim("name", token.getName())
                .claim("email", token.getEmail())
                .compact();
    }

    public String createBy(AppUser user) {
        return createBy(new AuthUserToken(user));
    }

    public Optional<AuthUserToken> parse(String token) {
        try {
            final var claims = Jwts.parser().verifyWith(SIGN_KEY).build().parseSignedClaims(token);

            return Optional.of(
                    new AuthUserToken(
                            claims.getPayload().get("id", String.class),
                            claims.getPayload().get("name", String.class),
                            claims.getPayload().get("email", String.class)
                    )
            );
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
