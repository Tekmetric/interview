package com.interview.service.context;

import com.interview.jpa.entity.User;
import com.interview.jpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserContext {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        String username = getUsernameOrNull();
        if (username == null) {
            throw new IllegalStateException("No authenticated user");
        }

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in DB"));
    }

    private String getUsernameOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth == null) ? null : auth.getName();
    }
}
