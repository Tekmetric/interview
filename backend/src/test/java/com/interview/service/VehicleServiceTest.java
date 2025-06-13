package com.interview.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.interview.dto.CustomerSummaryDTO;
import com.interview.dto.VehicleRequestDTO;
import com.interview.dto.VehicleResponseDTO;
import com.interview.dto.VehicleSummaryDTO;
import com.interview.exception.ResourceAlreadyExistsException;
import com.interview.exception.ResourceNotFoundException;
import com.interview.mapper.VehicleMapper;
import com.interview.model.CustomerEntity;
import com.interview.model.VehicleEntity;
import com.interview.repository.CustomerRepository;
import com.interview.repository.VehicleRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@DisplayName("VehicleService Tests")
class VehicleServiceTest {

  @Mock
  private VehicleRepository vehicleRepository;

  @Mock
  private CustomerRepository customerRepository;

  @Mock
  private VehicleMapper vehicleMapper;

  @InjectMocks
  private VehicleService vehicleService;

  private VehicleEntity vehicleEntity;
  private CustomerEntity customerEntity;
  private VehicleRequestDTO vehicleRequestDTO;
  private VehicleResponseDTO vehicleResponseDTO;
  private VehicleSummaryDTO vehicleSummaryDTO;

  @BeforeEach
  void setUp() {
    customerEntity = CustomerEntity.builder()
        .id(1L)
        .name("John Doe")
        .email("john@example.com")
        .build();

    vehicleEntity = VehicleEntity.builder()
        .id(1L)
        .customer(customerEntity)
        .make("Toyota")
        .model("Camry")
        .year(2020)
        .licensePlate("ABC123")
        .build();

    vehicleRequestDTO = new VehicleRequestDTO(
        1L, // customerId
        "Toyota",
        "Camry",
        2020,
        "ABC123"
    );

    CustomerSummaryDTO customerSummaryDTO = new CustomerSummaryDTO(
        1L,
        "John Doe",
        "john@example.com",
        null,
        1
    );

    vehicleResponseDTO = new VehicleResponseDTO(
        1L,
        "Toyota",
        "Camry",
        2020,
        "ABC123",
        customerSummaryDTO,
        null
    );

    vehicleSummaryDTO = new VehicleSummaryDTO(
        1L,
        "Toyota",
        "Camry",
        2020,
        "ABC123",
        0
    );
  }

  @Nested
  @DisplayName("Get Operations")
  class GetOperations {

    @Test
    @DisplayName("Should return all vehicles as summary DTOs")
    void shouldReturnAllVehicles() {
      // Given
      List<VehicleEntity> entities = Collections.singletonList(vehicleEntity);
      List<VehicleSummaryDTO> summaryDTOs = Collections.singletonList(vehicleSummaryDTO);

      when(vehicleRepository.findAllByOrderById()).thenReturn(entities);
      when(vehicleMapper.toSummaryDTOList(entities)).thenReturn(summaryDTOs);

      // When
      List<VehicleSummaryDTO> result = vehicleService.getAllVehicles();

      // Then
      assertThat(result).hasSize(1);
      assertThat(result.getFirst()).isEqualTo(vehicleSummaryDTO);
      verify(vehicleRepository).findAllByOrderById();
      verify(vehicleMapper).toSummaryDTOList(entities);
    }

    @Test
    @DisplayName("Should return paginated vehicles")
    void shouldReturnPaginatedVehicles() {
      // Given
      Pageable pageable = PageRequest.of(0, 10);
      Page<VehicleEntity> entityPage = new PageImpl<>(Collections.singletonList(vehicleEntity));

      when(vehicleRepository.findAll(pageable)).thenReturn(entityPage);
      when(vehicleMapper.toSummaryDTO(vehicleEntity)).thenReturn(vehicleSummaryDTO);

      // When
      Page<VehicleSummaryDTO> result = vehicleService.getAllVehicles(pageable);

      // Then
      assertThat(result.getContent()).hasSize(1);
      assertThat(result.getContent().getFirst()).isEqualTo(vehicleSummaryDTO);
      verify(vehicleRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Should return vehicle by ID")
    void shouldReturnVehicleById() {
      // Given
      when(vehicleRepository.findByIdWithCustomer(1L)).thenReturn(Optional.of(vehicleEntity));
      when(vehicleMapper.toResponseDTO(vehicleEntity)).thenReturn(vehicleResponseDTO);

      // When
      VehicleResponseDTO result = vehicleService.getVehicleById(1L);

      // Then
      assertThat(result).isEqualTo(vehicleResponseDTO);
      verify(vehicleRepository).findByIdWithCustomer(1L);
      verify(vehicleMapper).toResponseDTO(vehicleEntity);
    }

    @Test
    @DisplayName("Should throw exception when vehicle not found by ID")
    void shouldThrowExceptionWhenVehicleNotFoundById() {
      // Given
      when(vehicleRepository.findByIdWithCustomer(1L)).thenReturn(Optional.empty());

      // When & Then
      assertThatThrownBy(() -> vehicleService.getVehicleById(1L))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessage("Vehicle not found with id: 1");

      verify(vehicleRepository).findByIdWithCustomer(1L);
      verifyNoInteractions(vehicleMapper);
    }

    @Test
    @DisplayName("Should return vehicle by license plate")
    void shouldReturnVehicleByLicensePlate() {
      // Given
      String licensePlate = "ABC123";
      when(vehicleRepository.findByLicensePlate(licensePlate)).thenReturn(
          Optional.of(vehicleEntity));
      when(vehicleMapper.toResponseDTO(vehicleEntity)).thenReturn(vehicleResponseDTO);

      // When
      VehicleResponseDTO result = vehicleService.getVehicleByLicensePlate(licensePlate);

      // Then
      assertThat(result).isEqualTo(vehicleResponseDTO);
      verify(vehicleRepository).findByLicensePlate(licensePlate);
      verify(vehicleMapper).toResponseDTO(vehicleEntity);
    }

    @Test
    @DisplayName("Should return vehicles by customer ID")
    void shouldReturnVehiclesByCustomerId() {
      // Given
      Long customerId = 1L;
      List<VehicleEntity> entities = Collections.singletonList(vehicleEntity);
      List<VehicleSummaryDTO> summaryDTOs = Collections.singletonList(vehicleSummaryDTO);

      when(vehicleRepository.findByCustomerIdOrderById(customerId)).thenReturn(entities);
      when(vehicleMapper.toSummaryDTOList(entities)).thenReturn(summaryDTOs);

      // When
      List<VehicleSummaryDTO> result = vehicleService.getVehiclesByCustomerId(customerId);

      // Then
      assertThat(result).hasSize(1);
      assertThat(result.getFirst()).isEqualTo(vehicleSummaryDTO);
      verify(vehicleRepository).findByCustomerIdOrderById(customerId);
      verify(vehicleMapper).toSummaryDTOList(entities);
    }

  }

  @Nested
  @DisplayName("Create Operations")
  class CreateOperations {

    @Test
    @DisplayName("Should create vehicle successfully")
    void shouldCreateVehicleSuccessfully() {
      // Given
      when(vehicleRepository.existsByLicensePlate(vehicleRequestDTO.licensePlate())).thenReturn(
          false);
      when(customerRepository.findById(vehicleRequestDTO.customerId())).thenReturn(
          Optional.of(customerEntity));
      when(vehicleMapper.toEntity(vehicleRequestDTO, customerEntity)).thenReturn(vehicleEntity);
      when(vehicleRepository.save(vehicleEntity)).thenReturn(vehicleEntity);
      when(vehicleMapper.toResponseDTO(vehicleEntity)).thenReturn(vehicleResponseDTO);

      // When
      VehicleResponseDTO result = vehicleService.createVehicle(vehicleRequestDTO);

      // Then
      assertThat(result).isEqualTo(vehicleResponseDTO);
      verify(vehicleRepository).existsByLicensePlate(vehicleRequestDTO.licensePlate());
      verify(customerRepository).findById(vehicleRequestDTO.customerId());
      verify(vehicleMapper).toEntity(vehicleRequestDTO, customerEntity);
      verify(vehicleRepository).save(vehicleEntity);
      verify(vehicleMapper).toResponseDTO(vehicleEntity);
    }

    @Test
    @DisplayName("Should throw exception when license plate already exists")
    void shouldThrowExceptionWhenLicensePlateAlreadyExists() {
      // Given
      when(vehicleRepository.existsByLicensePlate(vehicleRequestDTO.licensePlate())).thenReturn(
          true);

      // When & Then
      assertThatThrownBy(() -> vehicleService.createVehicle(vehicleRequestDTO))
          .isInstanceOf(ResourceAlreadyExistsException.class)
          .hasMessage("Vehicle already exists with license plate: ABC123");

      verify(vehicleRepository).existsByLicensePlate(vehicleRequestDTO.licensePlate());
      verifyNoMoreInteractions(vehicleRepository);
      verifyNoInteractions(vehicleMapper);
    }
  }

  @Nested
  @DisplayName("Update Operations")
  class UpdateOperations {

    @Test
    @DisplayName("Should update vehicle successfully")
    void shouldUpdateVehicleSuccessfully() {
      // Given
      VehicleRequestDTO updateRequest = new VehicleRequestDTO(
          1L,
          "Honda",
          "Civic",
          2021,
          "ABC123" // Same license plate
      );

      when(vehicleRepository.findByIdWithCustomer(1L)).thenReturn(Optional.of(vehicleEntity));
      when(vehicleRepository.save(vehicleEntity)).thenReturn(vehicleEntity);
      when(vehicleMapper.toResponseDTO(vehicleEntity)).thenReturn(vehicleResponseDTO);

      // When
      VehicleResponseDTO result = vehicleService.updateVehicle(1L, updateRequest);

      // Then
      assertThat(result).isEqualTo(vehicleResponseDTO);
      verify(vehicleRepository).findByIdWithCustomer(1L);
      verify(vehicleMapper).updateEntityFromDTO(vehicleEntity, updateRequest, customerEntity);
      verify(vehicleRepository).save(vehicleEntity);
      verify(vehicleMapper).toResponseDTO(vehicleEntity);
    }

    @Test
    @DisplayName("Should throw exception when updating to existing license plate")
    void shouldThrowExceptionWhenUpdatingToExistingLicensePlate() {
      // Given
      VehicleRequestDTO updateRequest = new VehicleRequestDTO(
          1L,
          "Honda",
          "Civic",
          2021,
          "XYZ789" // Different license plate
      );

      when(vehicleRepository.findByIdWithCustomer(1L)).thenReturn(Optional.of(vehicleEntity));
      when(vehicleRepository.existsByLicensePlate("XYZ789")).thenReturn(true);

      // When & Then
      assertThatThrownBy(() -> vehicleService.updateVehicle(1L, updateRequest))
          .isInstanceOf(ResourceAlreadyExistsException.class)
          .hasMessage("Vehicle already exists with license plate: XYZ789");

      verify(vehicleRepository).findByIdWithCustomer(1L);
      verify(vehicleRepository).existsByLicensePlate("XYZ789");
      verifyNoMoreInteractions(vehicleRepository);
    }
  }

  @Nested
  @DisplayName("Delete Operations")
  class DeleteOperations {

    @Test
    @DisplayName("Should delete vehicle successfully")
    void shouldDeleteVehicleSuccessfully() {
      // Given
      when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicleEntity));

      // When
      vehicleService.deleteVehicle(1L);

      // Then
      verify(vehicleRepository).findById(1L);
      verify(vehicleRepository).delete(vehicleEntity);
    }

    @Test
    @DisplayName("Should throw exception when vehicle to delete not found")
    void shouldThrowExceptionWhenVehicleToDeleteNotFound() {
      // Given
      when(vehicleRepository.findById(1L)).thenReturn(Optional.empty());

      // When & Then
      assertThatThrownBy(() -> vehicleService.deleteVehicle(1L))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessage("Vehicle not found with id: 1");

      verify(vehicleRepository).findById(1L);
      verify(vehicleRepository, never()).delete(any());
    }
  }

}
