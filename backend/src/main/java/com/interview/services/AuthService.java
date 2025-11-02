package com.interview.services;

import com.interview.models.user.User;
import com.interview.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> authenticate(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Basic ")) {
            return Optional.empty();
        }
        try {
            String base64Credentials = authorizationHeader.substring("Basic ".length()).trim();
            byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(decodedBytes, StandardCharsets.UTF_8);
            int idx = credentials.indexOf(":");
            if (idx < 0) return Optional.empty();
            String username = credentials.substring(0, idx);
            String password = credentials.substring(idx + 1);

            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) return Optional.empty();
            User user = userOpt.get();
            if (user.getPasswordKey() == null || user.getPassword() == null) return Optional.empty();
            String computed = CryptoUtil.hmacPasswordB64(password, user.getPasswordKey());
            if (computed.equals(user.getPassword())) {
                return Optional.of(user);
            }
            return Optional.empty();
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}