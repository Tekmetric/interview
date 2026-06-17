package com.interview.service;

import com.interview.dto.VehicleRequest;
import com.interview.dto.VehicleResponse;
import com.interview.dto.filter.VehicleFilter;
import com.interview.entity.Customer;
import com.interview.entity.Vehicle;
import com.interview.exception.BadRequestException;
import com.interview.exception.CustomerNotFoundException;
import com.interview.exception.VehicleNotFoundException;
import com.interview.mapper.VehicleMapper;
import com.interview.repository.CustomerRepository;
import com.interview.repository.VehicleRepository;
import com.interview.specification.VehicleSpecs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("VehicleService Unit Tests")
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private VehicleMapper vehicleMapper;

    @InjectMocks
    private VehicleService vehicleService;

    private Vehicle testVehicle;
    private Customer testCustomer;
    private VehicleRequest testRequest;
    private VehicleResponse testResponse;

    @BeforeEach
    void setUp() {
        // Test customer
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Doe");
        testCustomer.setEmail("john.doe@example.com");

        // Test vehicle
        testVehicle = new Vehicle();
        testVehicle.setId(1L);
        testVehicle.setCustomer(testCustomer);
        testVehicle.setVin("1HGCM82633A123456");
        testVehicle.setMake("Honda");
        testVehicle.setModel("Accord");
        testVehicle.setYear(2020);
        testVehicle.setCreatedDate(LocalDateTime.now());
        testVehicle.setUpdatedDate(LocalDateTime.now());

        // Test DTOs
        testRequest = new VehicleRequest(
            1L,
            "1HGCM82633A123456",
            "Honda",
            "Accord",
            2020
        );

        testResponse = new VehicleResponse(
            1L,
            1L,
            "John Doe",
            "john.doe@example.com",
            "1HGCM82633A123456",
            "Honda",
            "Accord",
            2020,
            LocalDateTime.now(),
            LocalDateTime.now(),
            "admin",
            "admin"
        );
    }

    @Nested
    @DisplayName("Create Vehicle Tests")
    class CreateVehicleTests {

        @Test
        @DisplayName("Should create vehicle successfully")
        void shouldCreateVehicleSuccessfully() {
            when(vehicleRepository.existsByVin(testRequest.vin())).thenReturn(false);
            when(customerRepository.existsById(testRequest.customerId())).thenReturn(true);
            when(vehicleMapper.toEntity(testRequest)).thenReturn(testVehicle);
            when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);
            when(vehicleMapper.toCreateResponse(testVehicle)).thenReturn(testResponse);

            VehicleResponse result = vehicleService.createVehicle(testRequest);

            assertThat(result).isNotNull();
            assertThat(result.vin()).isEqualTo(testRequest.vin());
            assertThat(result.customerId()).isEqualTo(testRequest.customerId());
            verify(vehicleRepository).existsByVin(testRequest.vin());
            verify(customerRepository).existsById(testRequest.customerId());
            verify(vehicleRepository).save(any(Vehicle.class));
        }

        @Test
        @DisplayName("Should throw exception when VIN already exists")
        void shouldThrowExceptionWhenVinExists() {
            when(vehicleRepository.existsByVin(testRequest.vin())).thenReturn(true);

            assertThatThrownBy(() -> vehicleService.createVehicle(testRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("VIN " + testRequest.vin() + " already exists");

            verify(customerRepository, never()).existsById(anyLong());
            verify(vehicleRepository, never()).save(any(Vehicle.class));
        }

        @Test
        @DisplayName("Should throw exception when customer not found")
        void shouldThrowExceptionWhenCustomerNotFound() {
            when(vehicleRepository.existsByVin(testRequest.vin())).thenReturn(false);
            when(customerRepository.existsById(testRequest.customerId())).thenReturn(false);

            assertThatThrownBy(() -> vehicleService.createVehicle(testRequest))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining(testRequest.customerId().toString());

            verify(vehicleRepository, never()).save(any(Vehicle.class));
        }
    }

    @Nested
    @DisplayName("Get Vehicle Tests")
    class GetVehicleTests {

        @Test
        @DisplayName("Should get vehicle by ID successfully")
        void shouldGetVehicleById() {
            when(vehicleRepository.findByIdWithCustomer(1L)).thenReturn(Optional.of(testVehicle));
            when(vehicleMapper.toResponse(testVehicle)).thenReturn(testResponse);

            VehicleResponse result = vehicleService.getVehicleById(1L);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            verify(vehicleRepository).findByIdWithCustomer(1L);
        }

        @Test
        @DisplayName("Should throw exception when vehicle not found")
        void shouldThrowExceptionWhenVehicleNotFound() {
            when(vehicleRepository.findByIdWithCustomer(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> vehicleService.getVehicleById(1L))
                .isInstanceOf(VehicleNotFoundException.class)
                .hasMessageContaining("1");
        }

        @Test
        @DisplayName("Should get all vehicles successfully")
        void shouldGetAllVehicles() {
            List<Vehicle> vehicles = List.of(testVehicle);
            when(vehicleRepository.findAllWithCustomers()).thenReturn(vehicles);
            when(vehicleMapper.toResponseList(vehicles)).thenReturn(List.of(testResponse));

            List<VehicleResponse> result = vehicleService.getAllVehicles();

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().id()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should get vehicles with pagination successfully")
        void shouldGetVehiclesWithPagination() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Vehicle> vehiclePage = new PageImpl<>(List.of(testVehicle), pageable, 1);
            when(vehicleRepository.findAllWithCustomers(pageable)).thenReturn(vehiclePage);
            when(vehicleMapper.toResponse(testVehicle)).thenReturn(testResponse);

            Page<VehicleResponse> result = vehicleService.getVehiclesWithPagination(pageable);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should search vehicles with filters successfully")
        void shouldSearchVehiclesWithFilters() {
            VehicleFilter filter = VehicleFilter.builder()
                .customerId(1L)
                .make("Honda")
                .build();
            Pageable pageable = PageRequest.of(0, 10);
            Specification<Vehicle> mockSpec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
            Page<Vehicle> vehiclePage = new PageImpl<>(List.of(testVehicle), pageable, 1);

            try (MockedStatic<VehicleSpecs> mockedSpecs = mockStatic(VehicleSpecs.class)) {
                mockedSpecs.when(() -> VehicleSpecs.getVehiclesByFilters(filter)).thenReturn(mockSpec);
                when(vehicleRepository.findAll(mockSpec, pageable)).thenReturn(vehiclePage);
                when(vehicleMapper.toResponse(testVehicle)).thenReturn(testResponse);

                Page<VehicleResponse> result = vehicleService.searchVehicles(filter, pageable);

                assertThat(result.getContent()).hasSize(1);
                assertThat(result.getTotalElements()).isEqualTo(1);
                mockedSpecs.verify(() -> VehicleSpecs.getVehiclesByFilters(filter));
            }
        }
    }

    @Nested
    @DisplayName("Update Vehicle Tests")
    class UpdateVehicleTests {

        @Test
        @DisplayName("Should update vehicle successfully")
        void shouldUpdateVehicleSuccessfully() {
            when(vehicleRepository.findByIdWithCustomer(1L)).thenReturn(Optional.of(testVehicle));
            when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);
            when(vehicleMapper.toResponse(testVehicle)).thenReturn(testResponse);

            VehicleResponse result = vehicleService.updateVehicle(1L, testRequest);

            assertThat(result).isNotNull();
            verify(vehicleMapper).updateEntity(testVehicle, testRequest);
            verify(vehicleRepository).save(testVehicle);
        }

        @Test
        @DisplayName("Should throw exception when vehicle not found for update")
        void shouldThrowExceptionWhenVehicleNotFoundForUpdate() {
            when(vehicleRepository.findByIdWithCustomer(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> vehicleService.updateVehicle(1L, testRequest))
                .isInstanceOf(VehicleNotFoundException.class);
        }

        @Test
        @DisplayName("Should throw exception when new VIN already exists")
        void shouldThrowExceptionWhenNewVinExists() {
            testVehicle.setVin("DIFFERENT_VIN"); // Current VIN is different
            when(vehicleRepository.findByIdWithCustomer(1L)).thenReturn(Optional.of(testVehicle));
            when(vehicleRepository.existsByVin(testRequest.vin())).thenReturn(true);

            assertThatThrownBy(() -> vehicleService.updateVehicle(1L, testRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("VIN " + testRequest.vin() + " already exists");
        }

        @Test
        @DisplayName("Should throw exception when new customer not found")
        void shouldThrowExceptionWhenNewCustomerNotFound() {
            testVehicle.getCustomer().setId(2L); // Different customer ID
            when(vehicleRepository.findByIdWithCustomer(1L)).thenReturn(Optional.of(testVehicle));
            when(customerRepository.existsById(testRequest.customerId())).thenReturn(false);

            assertThatThrownBy(() -> vehicleService.updateVehicle(1L, testRequest))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining(testRequest.customerId().toString());
        }

        @Test
        @DisplayName("Should not check customer existence when customer ID unchanged")
        void shouldNotCheckCustomerWhenUnchanged() {
            when(vehicleRepository.findByIdWithCustomer(1L)).thenReturn(Optional.of(testVehicle));
            when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);
            when(vehicleMapper.toResponse(testVehicle)).thenReturn(testResponse);

            vehicleService.updateVehicle(1L, testRequest);

            verify(customerRepository, never()).existsById(anyLong());
        }
    }

    @Nested
    @DisplayName("Delete Vehicle Tests")
    class DeleteVehicleTests {

        @Test
        @DisplayName("Should delete vehicle successfully")
        void shouldDeleteVehicleSuccessfully() {
            when(vehicleRepository.deleteByVehicleId(1L)).thenReturn(1);

            vehicleService.deleteVehicle(1L);

            verify(vehicleRepository).deleteByVehicleId(1L);
        }

        @Test
        @DisplayName("Should throw exception when vehicle not found for deletion")
        void shouldThrowExceptionWhenVehicleNotFoundForDeletion() {
            when(vehicleRepository.deleteByVehicleId(1L)).thenReturn(0);

            assertThatThrownBy(() -> vehicleService.deleteVehicle(1L))
                .isInstanceOf(VehicleNotFoundException.class)
                .hasMessageContaining("1");
        }
    }
}