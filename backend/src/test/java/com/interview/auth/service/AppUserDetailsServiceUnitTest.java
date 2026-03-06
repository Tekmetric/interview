package com.interview.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.interview.auth.dao.UserRepository;
import com.interview.auth.entity.User;
import com.interview.auth.model.UserRole;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class AppUserDetailsServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    private AppUserDetailsService service;

    @BeforeEach
    void setUp() {
        service = new AppUserDetailsService(userRepository);
    }

    @Test
    void shouldLoadUserByUsername() {
        User user = new User();
        user.setUsername("api-admin");
        user.setPasswordHash("$2a$10$9H11gZbYG/bWcdM6nD/Sg.CwbM9Col.hW2bgTdwJ6vi5TzWpd6SOi");
        user.setRole(UserRole.ADMIN);
        user.setEnabled(true);

        when(userRepository.findByUsername("api-admin")).thenReturn(Optional.of(user));

        UserDetails userDetails = service.loadUserByUsername("api-admin");

        assertThat(userDetails.getUsername()).isEqualTo("api-admin");
        assertThat(userDetails.getPassword()).isEqualTo(user.getPasswordHash());
        assertThat(userDetails.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_ADMIN");
        assertThat(userDetails.isEnabled()).isTrue();
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findByUsername("missing-user")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("missing-user"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found: missing-user");
    }
}
