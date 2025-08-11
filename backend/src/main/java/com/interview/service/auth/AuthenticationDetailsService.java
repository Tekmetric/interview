package com.interview.service.auth;

import com.interview.jpa.entity.User;
import com.interview.jpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User appUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Map your domain Role to Spring authorities (e.g., "ROLE_ADMIN")
        String roleName = appUser.getRole() != null ? "ROLE_" + appUser.getRole().name() : "ROLE_USER";
        boolean enabled = appUser.getStatus() == com.interview.jpa.entity.enums.UserEnum.Status.ACTIVE;

        return org.springframework.security.core.userdetails.User.withUsername(appUser.getUsername())
                .password(appUser.getPasswordHash())
                .authorities(List.of(new SimpleGrantedAuthority(roleName)))
                .disabled(!enabled)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .build();
    }
}
