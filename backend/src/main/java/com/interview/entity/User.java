package com.interview.entity;

import com.interview.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Simple user entity for authentication and authorization.
 *
 * <p>This is a lightweight implementation for demonstration purposes.
 *
 * <p><strong>Production Considerations:</strong>
 * <ul>
 *   <li><strong>Database Storage:</strong> Users would be persisted in database with proper JPA annotations</li>
 *   <li><strong>Password Security:</strong> Passwords would be hashed using BCrypt or Argon2</li>
 *   <li><strong>Identity Providers:</strong> Could integrate with external identity management:
 *     <ul>
 *       <li>Keycloak for enterprise SSO and user federation</li>
 *       <li>Auth0 for cloud-based identity as a service</li>
 *       <li>AWS Cognito for AWS-native user pools</li>
 *       <li>Azure AD for Microsoft ecosystem integration</li>
 *     </ul>
 *   </li>
 * </ul>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private String username;
    private String password;
    private Role role;
}