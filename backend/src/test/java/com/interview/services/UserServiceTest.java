package com.interview.services;

import com.interview.models.user.Gender;
import com.interview.models.user.User;
import com.interview.models.user.dto.CreateUserRequest;
import com.interview.models.user.dto.UpdateUserRequest;
import com.interview.models.user.dto.UserResponse;
import com.interview.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    void createUser_shouldHashPassword_SetKey_SanitizeAndReturnResponse() throws Exception {
        CreateUserRequest req = new CreateUserRequest(
                "John",
                "M",
                "Doe",
                "johndoe",
                "SecurePass123$",
                LocalDate.of(1990, 1, 15),
                "123-45-6789",
                Gender.MALE,
                "john.doe@example.com",
                "555-010-8888"
        );

        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });

        UserResponse resp = userService.createUser(req);

        // Verify repository save called with sanitized values and hashed password
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User saved = userCaptor.getValue();

        assertThat(saved.getPassword()).isNotBlank();
        assertThat(saved.getPasswordKey()).isNotBlank();
        // Should not be raw password
        assertThat(saved.getPassword()).isNotEqualTo(req.password());

        // SSN and phone should be digits only
        assertThat(saved.getSsn()).isEqualTo("123456789");
        assertThat(saved.getPhoneNumber()).isEqualTo("5550108888");

        // Response should not contain sensitive data and echo normalized phone
        assertThat(resp.id()).isEqualTo(1L);
        assertThat(resp.username()).isEqualTo("johndoe");
        assertThat(resp.phoneNumber()).isEqualTo("5550108888");
        assertThat(resp.email()).isEqualTo("john.doe@example.com");
        assertThat(resp.gender()).isEqualTo(Gender.MALE);
    }

    @Test
    void updateUser_shouldSanitizeAndOptionallyHashPassword() {
        User existing = new User();
        existing.setId(5L);
        existing.setUsername("jane");
        existing.setPhoneNumber("1112223333");
        existing.setSsn("000000000");

        when(userRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateUserRequest update = new UpdateUserRequest(
                null, // firstName
                null, // middleName
                null, // lastName
                null, // username
                "NewPassw0rd$",
                null, // dob
                "987-65-4321",
                null, // gender
                null, // email
                "999-888-7777"
        );

        Optional<UserResponse> updated = userService.updateUser(5L, update);
        assertThat(updated).isPresent();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User saved = userCaptor.getValue();

        // Sanitized digits
        assertThat(saved.getSsn()).isEqualTo("987654321");
        assertThat(saved.getPhoneNumber()).isEqualTo("9998887777");

        // Password should have been re-hashed and key regenerated
        assertThat(saved.getPassword()).isNotBlank();
        assertThat(saved.getPasswordKey()).isNotBlank();
    }

    @Test
    void getUserById_shouldMapToResponse() {
        User u = new User();
        u.setId(10L);
        u.setFirstName("Alice");
        u.setLastName("Smith");
        u.setUsername("alice");
        u.setEmail("alice@example.com");
        u.setGender(Gender.FEMALE);

        when(userRepository.findById(10L)).thenReturn(Optional.of(u));

        Optional<UserResponse> resp = userService.getUserById(10L);
        assertThat(resp).isPresent();
        assertThat(resp.get().id()).isEqualTo(10L);
        assertThat(resp.get().username()).isEqualTo("alice");
        assertThat(resp.get().email()).isEqualTo("alice@example.com");
    }
}
