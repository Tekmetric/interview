package com.interview.service;

import com.interview.dto.PaginatedResponse;
import com.interview.dto.VehicleFilterRequest;
import com.interview.dto.VehicleRequest;
import com.interview.dto.VehicleResponse;
import com.interview.exception.AccessDeniedException;
import com.interview.exception.BusinessRuleViolationException;
import com.interview.exception.DuplicateEntityException;
import com.interview.exception.NotFoundException;
import com.interview.mapper.VehicleMapper;
import com.interview.model.Role;
import com.interview.model.User;
import com.interview.model.Vehicle;
import com.interview.repository.UserRepository;
import com.interview.repository.VehicleRepository;
import com.interview.security.service.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private VehicleMapper vehicleMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private VehicleService vehicleService;

    private User owner;
    private User admin;
    private Vehicle vehicle;

    @BeforeEach
    void setUp() {
        owner = User.builder().id(1L).username("owner").role(Role.CUSTOMER).build();
        admin = User.builder().id(2L).username("admin").role(Role.ADMIN).build();

        vehicle = Vehicle.builder()
                .id(1L)
                .brand("Toyota")
                .model("Camry")
                .registrationYear(2022)
                .licensePlate("ABC123")
                .owner(owner)
                .build();
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
        when(vehicleRepository.findByLicensePlate("XYZ789")).thenReturn(Optional.empty());
        when(vehicleRepository.save(any())).thenReturn(vehicle);
        when(vehicleMapper.toResponse(any())).thenReturn(new VehicleResponse());

        VehicleResponse result = vehicleService.createVehicle(request);

        assertNotNull(result);
        verify(vehicleRepository).save(any());
    }

    @Test
    void createVehicle_duplicatePlate() {
        VehicleRequest request = VehicleRequest.builder()
                .licensePlate("ABC123")
                .build();

        when(securityService.getCurrentUser()).thenReturn(owner);
        when(vehicleRepository.findByLicensePlate("ABC123")).thenReturn(Optional.of(vehicle));

        assertThrows(DuplicateEntityException.class, () -> vehicleService.createVehicle(request));
        verify(vehicleRepository, never()).save(any());
    }

    @Test
    void getAllVehicles() {
        Page<Vehicle> page = new PageImpl<>(List.of(vehicle), PageRequest.of(0, 10), 1);
        when(vehicleRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Pageable.class))).thenReturn(page);
        when(vehicleMapper.toResponseList(any())).thenReturn(List.of(new VehicleResponse()));

        PaginatedResponse<VehicleResponse> result = vehicleService.getAllVehicles(
                VehicleFilterRequest.builder().build(),
                PageRequest.of(0, 10)
        );

        assertEquals(1, result.getContent().size());
    }

    @Test
    void getVehicleById() {
        when(securityService.getCurrentUser()).thenReturn(owner);
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(vehicleMapper.toResponse(vehicle)).thenReturn(new VehicleResponse());

        VehicleResponse result = vehicleService.getVehicleById(1L);

        assertNotNull(result);
    }

    @Test
    void getVehicleById_adminCanAccess() {
        when(securityService.getCurrentUser()).thenReturn(admin);
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(vehicleMapper.toResponse(vehicle)).thenReturn(new VehicleResponse());

        VehicleResponse result = vehicleService.getVehicleById(1L);

        assertNotNull(result);
    }

    @Test
    void getVehicleById_unauthorized() {
        User other = User.builder().id(3L).role(Role.CUSTOMER).build();
        when(securityService.getCurrentUser()).thenReturn(other);
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        assertThrows(AccessDeniedException.class, () -> vehicleService.getVehicleById(1L));
    }

    @Test
    void getVehicleById_notFound() {
        when(securityService.getCurrentUser()).thenReturn(owner);
        when(vehicleRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> vehicleService.getVehicleById(999L));
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
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(any())).thenReturn(vehicle);
        when(vehicleMapper.toResponse(any())).thenReturn(new VehicleResponse());

        vehicleService.updateVehicle(1L, request);

        assertEquals("Honda", vehicle.getBrand());
        verify(vehicleRepository).save(vehicle);
    }

    @Test
    void updateVehicle_unauthorized() {
        User other = User.builder().id(3L).role(Role.CUSTOMER).build();
        when(securityService.getCurrentUser()).thenReturn(other);
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        assertThrows(AccessDeniedException.class, () -> vehicleService.updateVehicle(1L, new VehicleRequest()));
    }

    @Test
    void updateVehicle_duplicatePlate() {
        VehicleRequest request = VehicleRequest.builder()
                .licensePlate("XYZ999")
                .build();

        Vehicle existing = Vehicle.builder().id(2L).licensePlate("XYZ999").build();

        when(securityService.getCurrentUser()).thenReturn(owner);
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.findByLicensePlate("XYZ999")).thenReturn(Optional.of(existing));

        assertThrows(DuplicateEntityException.class, () -> vehicleService.updateVehicle(1L, request));
    }

    @Test
    void deleteVehicle() {
        when(securityService.getCurrentUser()).thenReturn(owner);
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(any())).thenReturn(vehicle);

        vehicleService.deleteVehicle(1L);

        assertNotNull(vehicle.getDeletedAt());
    }

    @Test
    void deleteVehicle_alreadyDeleted() {
        vehicle.setDeletedAt(LocalDateTime.now());
        when(securityService.getCurrentUser()).thenReturn(owner);
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        assertThrows(BusinessRuleViolationException.class, () -> vehicleService.deleteVehicle(1L));
    }

    @Test
    void deleteVehiclesByOwner() {
        Vehicle v1 = Vehicle.builder().id(1L).owner(owner).build();
        Vehicle v2 = Vehicle.builder().id(2L).owner(owner).build();

        when(vehicleRepository.findByOwner(owner)).thenReturn(List.of(v1, v2));
        when(vehicleRepository.saveAll(any())).thenReturn(List.of(v1, v2));

        vehicleService.deleteVehiclesByOwner(owner);

        assertNotNull(v1.getDeletedAt());
        assertNotNull(v2.getDeletedAt());
    }
}
