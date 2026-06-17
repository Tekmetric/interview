package com.interview.service;

import com.interview.dto.PaginatedResponse;
import com.interview.dto.VehicleFilterRequest;
import com.interview.dto.VehicleRequest;
import com.interview.dto.VehicleResponse;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class VehicleServiceIntegrationTest {

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private SecurityService securityService;

    private User owner;
    private User admin;
    private Vehicle vehicle;

    @BeforeEach
    void setUp() {
        vehicleRepository.deleteAll();
        userRepository.deleteAll();

        owner = userRepository.save(User.builder()
                .username("owner")
                .passwordHash("hash")
                .firstName("Owner")
                .lastName("User")
                .emailAddress("owner@test.com")
                .role(Role.CUSTOMER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());

        admin = userRepository.save(User.builder()
                .username("admin")
                .passwordHash("hash")
                .firstName("Admin")
                .lastName("User")
                .emailAddress("admin@test.com")
                .role(Role.ADMIN)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());

        vehicle = vehicleRepository.save(Vehicle.builder()
                .brand("Toyota")
                .model("Camry")
                .registrationYear(2022)
                .licensePlate("ABC123")
                .owner(owner)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
    }

    @Test
    void createVehicle() {
        VehicleRequest request = VehicleRequest.builder()
                .brand("Honda")
                .model("Civic")
                .registrationYear(2023)
                .licensePlate("XYZ789")
                .build();

        when(securityService.getCurrentUser()).thenReturn(owner);

        VehicleResponse result = vehicleService.createVehicle(request);

        assertEquals("Honda", result.getBrand());
        Vehicle saved = vehicleRepository.findById(result.getId()).orElse(null);
        assertNotNull(saved);
    }

    @Test
    void getAllVehicles() {
        PaginatedResponse<VehicleResponse> result = vehicleService.getAllVehicles(
                VehicleFilterRequest.builder().build(),
                PageRequest.of(0, 10)
        );

        assertFalse(result.getContent().isEmpty());
    }

    @Test
    void getAllVehicles_withFilter() {
        PaginatedResponse<VehicleResponse> result = vehicleService.getAllVehicles(
                VehicleFilterRequest.builder().brand("Toyota").build(),
                PageRequest.of(0, 10)
        );

        result.getContent().forEach(v -> assertEquals("Toyota", v.getBrand()));
    }

    @Test
    void getVehicleById() {
        when(securityService.getCurrentUser()).thenReturn(owner);

        VehicleResponse result = vehicleService.getVehicleById(vehicle.getId());

        assertEquals(vehicle.getId(), result.getId());
    }

    @Test
    void getVehicleById_admin() {
        when(securityService.getCurrentUser()).thenReturn(admin);

        VehicleResponse result = vehicleService.getVehicleById(vehicle.getId());

        assertNotNull(result);
    }

    @Test
    void updateVehicle() {
        VehicleRequest request = VehicleRequest.builder()
                .brand("Honda")
                .model("Civic")
                .registrationYear(2023)
                .licensePlate("ABC123")
                .build();

        when(securityService.getCurrentUser()).thenReturn(owner);

        vehicleService.updateVehicle(vehicle.getId(), request);

        Vehicle updated = vehicleRepository.findById(vehicle.getId()).orElse(null);
        assertNotNull(updated);
        assertEquals("Honda", updated.getBrand());
    }

    @Test
    void deleteVehicle() {
        when(securityService.getCurrentUser()).thenReturn(owner);

        vehicleService.deleteVehicle(vehicle.getId());

        Vehicle deleted = vehicleRepository.findById(vehicle.getId()).orElse(null);
        assertNotNull(deleted);
        assertNotNull(deleted.getDeletedAt());
    }

    @Test
    void deleteVehiclesByOwner() {
        Vehicle v2 = vehicleRepository.save(Vehicle.builder()
                .brand("Ford")
                .model("F150")
                .registrationYear(2021)
                .licensePlate("DEF456")
                .owner(owner)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());

        vehicleService.deleteVehiclesByOwner(owner);

        Vehicle deleted1 = vehicleRepository.findById(vehicle.getId()).orElse(null);
        Vehicle deleted2 = vehicleRepository.findById(v2.getId()).orElse(null);

        assertNotNull(deleted1);
        assertNotNull(deleted1.getDeletedAt());
        assertNotNull(deleted2);
        assertNotNull(deleted2.getDeletedAt());
    }
}
