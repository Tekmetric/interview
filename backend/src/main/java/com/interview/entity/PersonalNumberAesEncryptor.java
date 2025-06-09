package com.interview.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

@Converter
public class PersonalNumberAesEncryptor implements AttributeConverter<String, String> {

  private static final String ALGORITHM = "AES";

  // Must be 16 bytes for AES-128
  // Should be stored securely and not hard-coded in production code
  private static final String SECRET = "my_secret_123456";

  private static final SecretKeySpec keySpec = new SecretKeySpec(SECRET.getBytes(), ALGORITHM);

  @Override
  public String convertToDatabaseColumn(final String attribute) {
    if (attribute == null) {
      return null;
    }
    try {
      final Cipher cipher = Cipher.getInstance(ALGORITHM);
      cipher.init(Cipher.ENCRYPT_MODE, keySpec);
      final byte[] encrypted = cipher.doFinal(attribute.getBytes());
      return Base64.getEncoder().encodeToString(encrypted);
    } catch (Exception e) {
      throw new IllegalStateException("Error encrypting personal number", e);
    }
  }

  @Override
  public String convertToEntityAttribute(final String dbData) {
    if (dbData == null) {
      return null;
    }
    try {
      final Cipher cipher = Cipher.getInstance(ALGORITHM);
      cipher.init(Cipher.DECRYPT_MODE, keySpec);
      final byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(dbData));
      return new String(decrypted);
    } catch (Exception e) {
      throw new IllegalStateException("Error decrypting personal number", e);
    }
  }
}
