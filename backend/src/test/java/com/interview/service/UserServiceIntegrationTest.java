package com.interview.service;

import com.interview.dto.UserRequest;
import com.interview.dto.UserResponse;
import com.interview.model.Role;
import com.interview.model.User;
import com.interview.model.Vehicle;
import com.interview.repository.UserRepository;
import com.interview.repository.VehicleRepository;
import com.interview.security.service.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @MockBean
    private SecurityService securityService;

    private User testUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        vehicleRepository.deleteAll();
        userRepository.deleteAll();

        testUser = userRepository.save(User.builder()
                .username("john")
                .passwordHash("hash")
                .firstName("John")
                .lastName("Doe")
                .emailAddress("john@test.com")
                .role(Role.CUSTOMER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());

        adminUser = userRepository.save(User.builder()
                .username("admin")
                .passwordHash("hash")
                .firstName("Admin")
                .lastName("User")
                .emailAddress("admin@test.com")
                .role(Role.ADMIN)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
    }

    @Test
    void getAllUsers() {
        var result = userService.getAllUsers();
        assertTrue(result.size() >= 2);
    }

    @Test
    void getUserById() {
        UserResponse result = userService.getUserById(testUser.getId());
        assertEquals(testUser.getId(), result.getId());
        assertEquals("john", result.getUsername());
    }

    @Test
    void deleteUser() {
        Vehicle vehicle = vehicleRepository.save(Vehicle.builder()
                .brand("Toyota")
                .model("Camry")
                .registrationYear(2022)
                .licensePlate("ABC123")
                .owner(testUser)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());

        when(securityService.getCurrentUser()).thenReturn(adminUser);

        userService.deleteUser(testUser.getId());

        User deleted = userRepository.findById(testUser.getId()).orElse(null);
        assertNotNull(deleted);
        assertNotNull(deleted.getDeletedAt());

        Vehicle deletedVehicle = vehicleRepository.findById(vehicle.getId()).orElse(null);
        assertNotNull(deletedVehicle);
        assertNotNull(deletedVehicle.getDeletedAt());
    }

    @Test
    void getCurrentUser() {
        when(securityService.getCurrentUser()).thenReturn(testUser);
        UserResponse result = userService.getCurrentUser();
        assertEquals(testUser.getId(), result.getId());
    }

    @Test
    void updateCurrentUser() {
        UserRequest request = UserRequest.builder()
                .username("john_updated")
                .firstName("Johnny")
                .lastName("Doe")
                .emailAddress("john.new@test.com")
                .build();

        when(securityService.getCurrentUser()).thenReturn(testUser);

        UserResponse result = userService.updateCurrentUser(request);

        assertEquals("john_updated", result.getUsername());
        User updated = userRepository.findById(testUser.getId()).orElse(null);
        assertNotNull(updated);
        assertEquals("Johnny", updated.getFirstName());
    }
}
