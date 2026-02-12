package com.interview.service.auth;

import com.interview.entity.User;
import com.interview.enums.Role;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for managing user authentication and authorization.
 *
 * <p>Uses hardcoded users for demonstration purposes to showcase authentication flow.
 *
 * <p><strong>Production Implementation Options:</strong>
 * <ul>
 *   <li><strong>Database-backed:</strong> JPA repository with BCrypt password encoding</li>
 *   <li><strong>External Identity Providers:</strong> Keycloak, Auth0, AWS Cognito, Azure AD</li>
 * </ul>
 */
@Slf4j
@Service
public class UserService {

    // Demo users for interview purposes - replace with proper user management in production
    private static final List<User> USERS = List.of(
        User.builder()
            .username("admin")
            .password("admin123")
            .role(Role.ADMIN)
            .build(),
        User.builder()
            .username("user")
            .password("user123")
            .role(Role.USER)
            .build()
    );

    /**
     * Find user by username.
     */
    public Optional<User> findByUsername(String username) {
        log.debug("Looking up user: {}", username);

        return USERS.stream()
            .filter(user -> user.getUsername().equals(username))
            .findFirst();
    }

    /**
     * Validate user credentials.
     */
    public boolean validateCredentials(String username, String password) {
        log.debug("Validating credentials for user: {}", username);

        return findByUsername(username)
            .map(user -> user.getPassword().equals(password))
            .orElse(false);
    }

    /**
     * Get user role by username.
     */
    public Optional<Role> getUserRole(String username) {
        return findByUsername(username)
            .map(User::getRole);
    }
}