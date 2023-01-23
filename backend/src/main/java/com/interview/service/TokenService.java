package com.interview.service;

import com.interview.config.ApplicationProperties;
import com.interview.model.Token;
import com.interview.model.TokenType;
import com.interview.model.User;
import com.interview.repository.TokenRepository;
import com.interview.util.CryptoUtils;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.transaction.Transactional;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

@Service
public class TokenService {

    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);

    private final ApplicationProperties applicationProperties;
    private final TokenRepository tokenRepository;
    private final SecretKey secretKey;
    private final String algorithm;
    private final IvParameterSpec ivParameterSpec;

    public TokenService(ApplicationProperties applicationProperties, TokenRepository tokenRepository) throws NoSuchAlgorithmException {
        this.applicationProperties = applicationProperties;
        this.tokenRepository = tokenRepository;
        this.algorithm = "AES/CBC/PKCS5Padding";
        this.ivParameterSpec = CryptoUtils.generateInitializationVector();
        this.secretKey = CryptoUtils.generateKey(256);
    }

    public String createJwtTokenValue(UUID id, Duration expireIn) {
        return createJwtTokenValue(id.toString(), expireIn);
    }

    private String createJwtTokenValue(String subject, Duration expireIn) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expireIn.toMillis());
        try {
            return Jwts.builder()
                    .setSubject(CryptoUtils.encrypt(algorithm, subject, secretKey, ivParameterSpec))
                    .setIssuedAt(new Date())
                    .setExpiration(expiryDate)
                    .setIssuer("the-issuer")
                    .signWith(SignatureAlgorithm.HS512, applicationProperties.getAuth().getTokenSecret())
                    .compact();
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            throw new IllegalStateException("Error while creating jwt token");
        }
    }

    public UUID getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(applicationProperties.getAuth().getTokenSecret())
                .parseClaimsJws(token)
                .getBody();
        try {
            return UUID.fromString(CryptoUtils.decrypt(algorithm, claims.getSubject(), secretKey, ivParameterSpec));
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            throw new IllegalStateException("Error while getting id from token");
        }
    }

    public boolean validateJwtToken(String jwtToken) {
        try {
            Jwts.parser().setSigningKey(applicationProperties.getAuth().getTokenSecret()).parseClaimsJws(jwtToken);
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty.");
        }
        return false;
    }

    @Transactional
    public Token createToken(User user, Duration expireIn, TokenType tokenType) {
        String tokenValue = createJwtTokenValue(user.getId(), expireIn);
        Token token = new Token();
        token.setValue(tokenValue);
        token.setUser(user);
        token.setTokenType(tokenType);
        return tokenRepository.save(token);
    }

    public void delete(Token token) {
        tokenRepository.delete(token);
    }
}
