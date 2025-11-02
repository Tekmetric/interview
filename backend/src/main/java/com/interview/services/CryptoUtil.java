package com.interview.services;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Simple utility for password protection using per-user random keys and HMAC-SHA256.
 * We avoid reversible encryption and instead compute a keyed digest (HMAC) of the password.
 * The random key is stored per user, and the HMAC output is stored as the password field.
 */
public final class CryptoUtil {

    private static final String HMAC_ALGO = "HmacSHA256";
    private static final int KEY_BYTES = 32; // 256-bit key
    private static final SecureRandom RNG = new SecureRandom();

    private CryptoUtil() {}

    /**
     * Generate a new random key (Base64-encoded) to be stored alongside the user.
     */
    public static String generateRandomKeyB64() {
        byte[] key = new byte[KEY_BYTES];
        RNG.nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }

    /**
     * Compute Base64-encoded HMAC-SHA256 of the given cleartext using the provided Base64-encoded key.
     */
    public static String hmacPasswordB64(String clearPassword, String base64Key) {
        try {
            byte[] key = Base64.getDecoder().decode(base64Key);
            Mac mac = Mac.getInstance(HMAC_ALGO);
            mac.init(new SecretKeySpec(key, HMAC_ALGO));
            byte[] out = mac.doFinal(clearPassword.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(out);
        } catch (Exception e) {
            throw new RuntimeException("Failed to compute password HMAC", e);
        }
    }
}
