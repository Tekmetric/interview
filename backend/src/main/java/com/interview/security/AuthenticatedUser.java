package com.interview.security;

import com.interview.entity.UserRole;

public record AuthenticatedUser(Long id, String email, UserRole role) {
    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }
}
