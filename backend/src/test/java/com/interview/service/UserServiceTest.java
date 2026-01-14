package com.interview.service;

import com.interview.dto.UserRequest;
import com.interview.dto.UserResponse;
import com.interview.exception.BusinessRuleViolationException;
import com.interview.exception.DuplicateEntityException;
import com.interview.exception.NotFoundException;
import com.interview.mapper.UserMapper;
import com.interview.model.Role;
import com.interview.model.User;
import com.interview.repository.UserRepository;
import com.interview.security.service.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private VehicleService vehicleService;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("john")
                .passwordHash("hash")
                .firstName("John")
                .lastName("Doe")
                .emailAddress("john@test.com")
                .role(Role.CUSTOMER)
                .build();

        adminUser = User.builder()
                .id(2L)
                .username("admin")
                .role(Role.ADMIN)
                .build();
    }

    @Test
    void getAllUsers() {
        List<User> users = List.of(testUser);
        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toResponseList(users)).thenReturn(List.of(new UserResponse()));

        List<UserResponse> result = userService.getAllUsers();

        assertNotNull(result);
        verify(userRepository).findAll();
    }

    @Test
    void getUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.toResponse(testUser)).thenReturn(new UserResponse());

        UserResponse result = userService.getUserById(1L);

        assertNotNull(result);
    }

    @Test
    void getUserById_notFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(999L));
    }

    @Test
    void deleteUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(securityService.getCurrentUser()).thenReturn(adminUser);

        userService.deleteUser(1L);

        assertNotNull(testUser.getDeletedAt());
        verify(vehicleService).deleteVehiclesByOwner(testUser);
        verify(userRepository).save(testUser);
    }

    @Test
    void deleteUser_cannotDeleteSelf() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(securityService.getCurrentUser()).thenReturn(testUser);

        assertThrows(BusinessRuleViolationException.class, () -> userService.deleteUser(1L));
    }

    @Test
    void deleteUser_alreadyDeleted() {
        testUser.setDeletedAt(LocalDateTime.now());
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(securityService.getCurrentUser()).thenReturn(adminUser);

        assertThrows(BusinessRuleViolationException.class, () -> userService.deleteUser(1L));
    }

    @Test
    void getCurrentUser() {
        when(securityService.getCurrentUser()).thenReturn(testUser);
        when(userMapper.toResponse(testUser)).thenReturn(new UserResponse());

        UserResponse result = userService.getCurrentUser();

        assertNotNull(result);
    }

    @Test
    void updateCurrentUser() {
        UserRequest request = UserRequest.builder()
                .username("john_updated")
                .firstName("John")
                .lastName("Doe")
                .emailAddress("john.new@test.com")
                .build();

        when(securityService.getCurrentUser()).thenReturn(testUser);
        when(userRepository.findByUsername("john_updated")).thenReturn(Optional.empty());
        when(userRepository.findByEmailAddress("john.new@test.com")).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(testUser);
        when(userMapper.toResponse(testUser)).thenReturn(new UserResponse());

        userService.updateCurrentUser(request);

        assertEquals("john_updated", testUser.getUsername());
        verify(userRepository).save(testUser);
    }

    @Test
    void updateCurrentUser_duplicateUsername() {
        UserRequest request = UserRequest.builder()
                .username("existing")
                .build();

        User existing = User.builder().id(99L).username("existing").build();

        when(securityService.getCurrentUser()).thenReturn(testUser);
        when(userRepository.findByUsername("existing")).thenReturn(Optional.of(existing));

        assertThrows(DuplicateEntityException.class, () -> userService.updateCurrentUser(request));
    }

    @Test
    void updateCurrentUser_sameUsername() {
        UserRequest request = UserRequest.builder()
                .username("john")
                .firstName("Johnny")
                .emailAddress("john@test.com")
                .build();

        when(securityService.getCurrentUser()).thenReturn(testUser);
        when(userRepository.save(any())).thenReturn(testUser);
        when(userMapper.toResponse(testUser)).thenReturn(new UserResponse());

        userService.updateCurrentUser(request);

        assertEquals("Johnny", testUser.getFirstName());
    }
}
