package com.interview.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.interview.dto.CustomerSummaryDTO;
import com.interview.dto.RepairOrderRequestDTO;
import com.interview.dto.RepairOrderResponseDTO;
import com.interview.dto.RepairOrderSummaryDTO;
import com.interview.dto.VehicleDetailsDTO;
import com.interview.exception.ResourceNotFoundException;
import com.interview.mapper.RepairOrderMapper;
import com.interview.model.CustomerEntity;
import com.interview.model.RepairOrderEntity;
import com.interview.model.RepairOrderStatus;
import com.interview.model.VehicleEntity;
import com.interview.repository.RepairOrderRepository;
import com.interview.repository.VehicleRepository;
import java.time.OffsetDateTime;
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
@DisplayName("RepairOrderService Tests")
class RepairOrderServiceTest {

  @Mock
  private RepairOrderRepository repairOrderRepository;

  @Mock
  private RepairOrderMapper repairOrderMapper;

  @Mock
  private VehicleRepository vehicleRepository;

  @InjectMocks
  private RepairOrderService repairOrderService;

  private RepairOrderEntity repairOrderEntity;
  private VehicleEntity vehicleEntity;
  private RepairOrderRequestDTO repairOrderRequestDTO;
  private RepairOrderResponseDTO repairOrderResponseDTO;
  private RepairOrderSummaryDTO repairOrderSummaryDTO;

  @BeforeEach
  void setUp() {
    OffsetDateTime now = OffsetDateTime.now();

    CustomerEntity customerEntity = CustomerEntity.builder()
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

    repairOrderEntity = RepairOrderEntity.builder()
        .id(1L)
        .vehicle(vehicleEntity)
        .description("Oil change and filter replacement")
        .status(RepairOrderStatus.PENDING)
        .createdDate(now)
        .updatedDate(now)
        .build();

    repairOrderRequestDTO = new RepairOrderRequestDTO(
        1L, // vehicleId
        "Oil change and filter replacement",
        RepairOrderStatus.PENDING
    );

    CustomerSummaryDTO customerSummaryDTO = new CustomerSummaryDTO(
        1L,
        "John Doe",
        "john@example.com",
        null,
        1
    );

    VehicleDetailsDTO vehicleDetailsDTO = new VehicleDetailsDTO(
        1L,
        "Toyota",
        "Camry",
        2020,
        "ABC123",
        customerSummaryDTO
    );

    repairOrderResponseDTO = new RepairOrderResponseDTO(
        1L,
        "Oil change and filter replacement",
        RepairOrderStatus.PENDING,
        now,
        now,
        vehicleDetailsDTO
    );

    repairOrderSummaryDTO = new RepairOrderSummaryDTO(
        1L,
        "Oil change and filter replacement",
        RepairOrderStatus.PENDING,
        now,
        now
    );
  }

  @Nested
  @DisplayName("Get Operations")
  class GetOperations {

    @Test
    @DisplayName("Should return all repair orders as summary DTOs")
    void shouldReturnAllRepairOrders() {
      // Given
      List<RepairOrderEntity> entities = Collections.singletonList(repairOrderEntity);
      List<RepairOrderSummaryDTO> summaryDTOs = Collections.singletonList(repairOrderSummaryDTO);

      when(repairOrderRepository.findAllByOrderById()).thenReturn(entities);
      when(repairOrderMapper.toSummaryDTOList(entities)).thenReturn(summaryDTOs);

      // When
      List<RepairOrderSummaryDTO> result = repairOrderService.getAllRepairOrders();

      // Then
      assertThat(result).hasSize(1);
      assertThat(result.getFirst()).isEqualTo(repairOrderSummaryDTO);
      verify(repairOrderRepository).findAllByOrderById();
      verify(repairOrderMapper).toSummaryDTOList(entities);
    }

    @Test
    @DisplayName("Should return paginated repair orders")
    void shouldReturnPaginatedRepairOrders() {
      // Given
      Pageable pageable = PageRequest.of(0, 10);
      Page<RepairOrderEntity> entityPage = new PageImpl<>(
          Collections.singletonList(repairOrderEntity));

      when(repairOrderRepository.findAll(pageable)).thenReturn(entityPage);
      when(repairOrderMapper.toSummaryDTO(repairOrderEntity)).thenReturn(repairOrderSummaryDTO);

      // When
      Page<RepairOrderSummaryDTO> result = repairOrderService.getAllRepairOrders(pageable);

      // Then
      assertThat(result.getContent()).hasSize(1);
      assertThat(result.getContent().getFirst()).isEqualTo(repairOrderSummaryDTO);
      verify(repairOrderRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Should return repair order by ID")
    void shouldReturnRepairOrderById() {
      // Given
      when(repairOrderRepository.findByIdWithVehicleAndCustomer(1L)).thenReturn(
          Optional.of(repairOrderEntity));
      when(repairOrderMapper.toResponseDTO(repairOrderEntity)).thenReturn(repairOrderResponseDTO);

      // When
      RepairOrderResponseDTO result = repairOrderService.getRepairOrderById(1L);

      // Then
      assertThat(result).isEqualTo(repairOrderResponseDTO);
      verify(repairOrderRepository).findByIdWithVehicleAndCustomer(1L);
      verify(repairOrderMapper).toResponseDTO(repairOrderEntity);
    }

    @Test
    @DisplayName("Should throw exception when repair order not found by ID")
    void shouldThrowExceptionWhenRepairOrderNotFoundById() {
      // Given
      when(repairOrderRepository.findByIdWithVehicleAndCustomer(1L)).thenReturn(Optional.empty());

      // When & Then
      assertThatThrownBy(() -> repairOrderService.getRepairOrderById(1L))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessage("Repair order not found with id: 1");

      verify(repairOrderRepository).findByIdWithVehicleAndCustomer(1L);
      verifyNoInteractions(repairOrderMapper);
    }

    @Test
    @DisplayName("Should return repair orders by vehicle ID")
    void shouldReturnRepairOrdersByVehicleId() {
      // Given
      Long vehicleId = 1L;
      List<RepairOrderEntity> entities = Collections.singletonList(repairOrderEntity);
      List<RepairOrderSummaryDTO> summaryDTOs = Collections.singletonList(repairOrderSummaryDTO);

      when(repairOrderRepository.findByVehicleIdOrderById(vehicleId)).thenReturn(entities);
      when(repairOrderMapper.toSummaryDTOList(entities)).thenReturn(summaryDTOs);

      // When
      List<RepairOrderSummaryDTO> result = repairOrderService.getRepairOrdersByVehicleId(vehicleId);

      // Then
      assertThat(result).hasSize(1);
      assertThat(result.getFirst()).isEqualTo(repairOrderSummaryDTO);
      verify(repairOrderRepository).findByVehicleIdOrderById(vehicleId);
      verify(repairOrderMapper).toSummaryDTOList(entities);
    }

    @Test
    @DisplayName("Should return repair orders by customer ID")
    void shouldReturnRepairOrdersByCustomerId() {
      // Given
      Long customerId = 1L;
      List<RepairOrderEntity> entities = Collections.singletonList(repairOrderEntity);
      List<RepairOrderSummaryDTO> summaryDTOs = Collections.singletonList(repairOrderSummaryDTO);

      when(repairOrderRepository.findByCustomerId(customerId)).thenReturn(entities);
      when(repairOrderMapper.toSummaryDTOList(entities)).thenReturn(summaryDTOs);

      // When
      List<RepairOrderSummaryDTO> result = repairOrderService.getRepairOrdersByCustomerId(
          customerId);

      // Then
      assertThat(result).hasSize(1);
      verify(repairOrderRepository).findByCustomerId(customerId);
      verify(repairOrderMapper).toSummaryDTOList(entities);
    }

    @Test
    @DisplayName("Should return repair orders by status")
    void shouldReturnRepairOrdersByStatus() {
      // Given
      RepairOrderStatus status = RepairOrderStatus.PENDING;
      List<RepairOrderEntity> entities = Collections.singletonList(repairOrderEntity);
      List<RepairOrderSummaryDTO> summaryDTOs = Collections.singletonList(repairOrderSummaryDTO);

      when(repairOrderRepository.findByStatus(status)).thenReturn(entities);
      when(repairOrderMapper.toSummaryDTOList(entities)).thenReturn(summaryDTOs);

      // When
      List<RepairOrderSummaryDTO> result = repairOrderService.getRepairOrdersByStatus(status);

      // Then
      assertThat(result).hasSize(1);
      verify(repairOrderRepository).findByStatus(status);
      verify(repairOrderMapper).toSummaryDTOList(entities);
    }

    @Test
    @DisplayName("Should return repair orders by vehicle license plate")
    void shouldReturnRepairOrdersByVehicleLicensePlate() {
      // Given
      String licensePlate = "ABC123";
      List<RepairOrderEntity> entities = Collections.singletonList(repairOrderEntity);
      List<RepairOrderSummaryDTO> summaryDTOs = Collections.singletonList(repairOrderSummaryDTO);

      when(repairOrderRepository.findByVehicleLicensePlate(licensePlate)).thenReturn(entities);
      when(repairOrderMapper.toSummaryDTOList(entities)).thenReturn(summaryDTOs);

      // When
      List<RepairOrderSummaryDTO> result = repairOrderService.getRepairOrdersByVehicleLicensePlate(
          licensePlate);

      // Then
      assertThat(result).hasSize(1);
      verify(repairOrderRepository).findByVehicleLicensePlate(licensePlate);
      verify(repairOrderMapper).toSummaryDTOList(entities);
    }

    @Test
    @DisplayName("Should search repair orders by description")
    void shouldSearchRepairOrdersByDescription() {
      // Given
      String description = "oil";
      List<RepairOrderEntity> entities = Collections.singletonList(repairOrderEntity);
      List<RepairOrderSummaryDTO> summaryDTOs = Collections.singletonList(repairOrderSummaryDTO);

      when(repairOrderRepository.findByDescriptionContainingIgnoreCase(description)).thenReturn(
          entities);
      when(repairOrderMapper.toSummaryDTOList(entities)).thenReturn(summaryDTOs);

      // When
      List<RepairOrderSummaryDTO> result = repairOrderService.searchRepairOrdersByDescription(
          description);

      // Then
      assertThat(result).hasSize(1);
      verify(repairOrderRepository).findByDescriptionContainingIgnoreCase(description);
      verify(repairOrderMapper).toSummaryDTOList(entities);
    }

    @Test
    @DisplayName("Should return repair orders by date range")
    void shouldReturnRepairOrdersByDateRange() {
      // Given
      OffsetDateTime startDate = OffsetDateTime.now().minusDays(7);
      OffsetDateTime endDate = OffsetDateTime.now();
      List<RepairOrderEntity> entities = Collections.singletonList(repairOrderEntity);
      List<RepairOrderSummaryDTO> summaryDTOs = Collections.singletonList(repairOrderSummaryDTO);

      when(repairOrderRepository.findByCreatedDateBetween(startDate, endDate)).thenReturn(entities);
      when(repairOrderMapper.toSummaryDTOList(entities)).thenReturn(summaryDTOs);

      // When
      List<RepairOrderSummaryDTO> result = repairOrderService.getRepairOrdersByDateRange(startDate,
          endDate);

      // Then
      assertThat(result).hasSize(1);
      verify(repairOrderRepository).findByCreatedDateBetween(startDate, endDate);
      verify(repairOrderMapper).toSummaryDTOList(entities);
    }

    @Test
    @DisplayName("Should return active repair orders")
    void shouldReturnActiveRepairOrders() {
      // Given
      List<RepairOrderEntity> entities = Collections.singletonList(repairOrderEntity);
      List<RepairOrderSummaryDTO> summaryDTOs = Collections.singletonList(repairOrderSummaryDTO);

      when(repairOrderRepository.findActiveRepairOrders()).thenReturn(entities);
      when(repairOrderMapper.toSummaryDTOList(entities)).thenReturn(summaryDTOs);

      // When
      List<RepairOrderSummaryDTO> result = repairOrderService.getActiveRepairOrders();

      // Then
      assertThat(result).hasSize(1);
      verify(repairOrderRepository).findActiveRepairOrders();
      verify(repairOrderMapper).toSummaryDTOList(entities);
    }

    @Test
    @DisplayName("Should return completed repair orders")
    void shouldReturnCompletedRepairOrders() {
      // Given
      List<RepairOrderEntity> entities = Collections.singletonList(repairOrderEntity);
      List<RepairOrderSummaryDTO> summaryDTOs = Collections.singletonList(repairOrderSummaryDTO);

      when(repairOrderRepository.findCompletedRepairOrdersOrderByUpdatedDate()).thenReturn(
          entities);
      when(repairOrderMapper.toSummaryDTOList(entities)).thenReturn(summaryDTOs);

      // When
      List<RepairOrderSummaryDTO> result = repairOrderService.getCompletedRepairOrders();

      // Then
      assertThat(result).hasSize(1);
      verify(repairOrderRepository).findCompletedRepairOrdersOrderByUpdatedDate();
      verify(repairOrderMapper).toSummaryDTOList(entities);
    }

    @Test
    @DisplayName("Should return recent repair orders by status")
    void shouldReturnRecentRepairOrdersByStatus() {
      // Given
      RepairOrderStatus status = RepairOrderStatus.PENDING;
      int days = 7;
      List<RepairOrderEntity> entities = Collections.singletonList(repairOrderEntity);
      List<RepairOrderSummaryDTO> summaryDTOs = Collections.singletonList(repairOrderSummaryDTO);

      when(repairOrderRepository.findByStatusAndCreatedDateAfter(eq(status),
          any(OffsetDateTime.class))).thenReturn(entities);
      when(repairOrderMapper.toSummaryDTOList(entities)).thenReturn(summaryDTOs);

      // When
      List<RepairOrderSummaryDTO> result = repairOrderService.getRecentRepairOrdersByStatus(status,
          days);

      // Then
      assertThat(result).hasSize(1);
      verify(repairOrderRepository).findByStatusAndCreatedDateAfter(eq(status),
          any(OffsetDateTime.class));
      verify(repairOrderMapper).toSummaryDTOList(entities);
    }
  }

  @Nested
  @DisplayName("Create Operations")
  class CreateOperations {

    @Test
    @DisplayName("Should create repair order successfully")
    void shouldCreateRepairOrderSuccessfully() {
      // Given
      when(vehicleRepository.findByIdWithCustomer(repairOrderRequestDTO.vehicleId())).thenReturn(
          Optional.of(vehicleEntity));
      when(repairOrderMapper.toEntity(repairOrderRequestDTO, vehicleEntity)).thenReturn(
          repairOrderEntity);
      when(repairOrderRepository.save(repairOrderEntity)).thenReturn(repairOrderEntity);
      when(repairOrderMapper.toResponseDTO(repairOrderEntity)).thenReturn(repairOrderResponseDTO);

      // When
      RepairOrderResponseDTO result = repairOrderService.createRepairOrder(repairOrderRequestDTO);

      // Then
      assertThat(result).isEqualTo(repairOrderResponseDTO);
      verify(vehicleRepository).findByIdWithCustomer(repairOrderRequestDTO.vehicleId());
      verify(repairOrderMapper).toEntity(repairOrderRequestDTO, vehicleEntity);
      verify(repairOrderRepository).save(repairOrderEntity);
      verify(repairOrderMapper).toResponseDTO(repairOrderEntity);
    }
  }

  @Nested
  @DisplayName("Update Operations")
  class UpdateOperations {

    @Test
    @DisplayName("Should update repair order successfully")
    void shouldUpdateRepairOrderSuccessfully() {
      // Given
      RepairOrderRequestDTO updateRequest = new RepairOrderRequestDTO(
          1L,
          "Updated description",
          RepairOrderStatus.IN_PROGRESS
      );

      when(repairOrderRepository.findByIdWithVehicleAndCustomer(1L)).thenReturn(
          Optional.of(repairOrderEntity));
      when(repairOrderRepository.save(repairOrderEntity)).thenReturn(repairOrderEntity);
      when(repairOrderMapper.toResponseDTO(repairOrderEntity)).thenReturn(repairOrderResponseDTO);

      // When
      RepairOrderResponseDTO result = repairOrderService.updateRepairOrder(1L, updateRequest);

      // Then
      assertThat(result).isEqualTo(repairOrderResponseDTO);
      verify(repairOrderRepository).findByIdWithVehicleAndCustomer(1L);
      verify(repairOrderMapper).updateEntityFromDTO(repairOrderEntity, updateRequest,
          vehicleEntity);
      verify(repairOrderRepository).save(repairOrderEntity);
      verify(repairOrderMapper).toResponseDTO(repairOrderEntity);
    }

    @Test
    @DisplayName("Should throw exception when repair order to update not found")
    void shouldThrowExceptionWhenRepairOrderToUpdateNotFound() {
      // Given
      RepairOrderRequestDTO updateRequest = new RepairOrderRequestDTO(1L, "Updated", RepairOrderStatus.IN_PROGRESS);
      when(repairOrderRepository.findByIdWithVehicleAndCustomer(1L)).thenReturn(Optional.empty());

      // When & Then
      assertThatThrownBy(() -> repairOrderService.updateRepairOrder(1L, updateRequest))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessage("Repair order not found with id: 1");

      verify(repairOrderRepository).findByIdWithVehicleAndCustomer(1L);
      verifyNoMoreInteractions(repairOrderRepository);
    }

    @Test
    @DisplayName("Should update repair order status successfully")
    void shouldUpdateRepairOrderStatusSuccessfully() {
      // Given
      RepairOrderStatus newStatus = RepairOrderStatus.COMPLETED;
      when(repairOrderRepository.findByIdWithVehicleAndCustomer(1L)).thenReturn(
          Optional.of(repairOrderEntity));
      when(repairOrderRepository.save(repairOrderEntity)).thenReturn(repairOrderEntity);
      when(repairOrderMapper.toResponseDTO(repairOrderEntity)).thenReturn(repairOrderResponseDTO);

      // When
      RepairOrderResponseDTO result = repairOrderService.updateRepairOrderStatus(1L, newStatus);

      // Then
      assertThat(result).isEqualTo(repairOrderResponseDTO);
      verify(repairOrderRepository).findByIdWithVehicleAndCustomer(1L);
      verify(repairOrderRepository).save(repairOrderEntity);
      verify(repairOrderMapper).toResponseDTO(repairOrderEntity);
    }
  }

  @Nested
  @DisplayName("Delete Operations")
  class DeleteOperations {

    @Test
    @DisplayName("Should delete repair order successfully")
    void shouldDeleteRepairOrderSuccessfully() {
      // Given
      when(repairOrderRepository.findById(1L)).thenReturn(Optional.of(repairOrderEntity));

      // When
      repairOrderService.deleteRepairOrder(1L);

      // Then
      verify(repairOrderRepository).findById(1L);
      verify(repairOrderRepository).delete(repairOrderEntity);
    }

    @Test
    @DisplayName("Should throw exception when repair order to delete not found")
    void shouldThrowExceptionWhenRepairOrderToDeleteNotFound() {
      // Given
      when(repairOrderRepository.findById(1L)).thenReturn(Optional.empty());

      // When & Then
      assertThatThrownBy(() -> repairOrderService.deleteRepairOrder(1L))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessage("Repair order not found with id: 1");

      verify(repairOrderRepository).findById(1L);
      verify(repairOrderRepository, never()).delete(any());
    }
  }
}
