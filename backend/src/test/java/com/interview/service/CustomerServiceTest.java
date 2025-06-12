package com.interview.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.interview.dto.CustomerRequestDTO;
import com.interview.dto.CustomerResponseDTO;
import com.interview.dto.CustomerSummaryDTO;
import com.interview.exception.ResourceAlreadyExistsException;
import com.interview.exception.ResourceNotFoundException;
import com.interview.mapper.CustomerMapper;
import com.interview.model.CustomerEntity;
import com.interview.repository.CustomerRepository;
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
@DisplayName("CustomerService Tests")
class CustomerServiceTest {

  @Mock
  private CustomerRepository customerRepository;

  @Mock
  private CustomerMapper customerMapper;

  @InjectMocks
  private CustomerService customerService;

  private CustomerEntity customerEntity;
  private CustomerRequestDTO customerRequestDTO;
  private CustomerResponseDTO customerResponseDTO;
  private CustomerSummaryDTO customerSummaryDTO;

  @BeforeEach
  void setUp() {
    customerEntity = CustomerEntity.builder()
        .id(1L)
        .name("John Doe")
        .email("john@example.com")
        .phoneNumber("123-456-7890")
        .address("123 Main St")
        .build();

    customerRequestDTO = new CustomerRequestDTO(
        "John Doe",
        "john@example.com",
        "123-456-7890",
        "123 Main St"
    );

    customerResponseDTO = new CustomerResponseDTO(
        1L,
        "John Doe",
        "john@example.com",
        "123-456-7890",
        "123 Main St",
        null
    );

    customerSummaryDTO = new CustomerSummaryDTO(
        1L,
        "John Doe",
        "john@example.com",
        "123-456-7890",
        0
    );
  }

  @Nested
  @DisplayName("Get Operations")
  class GetOperations {

    @Test
    @DisplayName("Should return all customers as summary DTOs")
    void shouldReturnAllCustomers() {
      // Given
      List<CustomerEntity> entities = Collections.singletonList(customerEntity);
      List<CustomerSummaryDTO> summaryDTOs = Collections.singletonList(customerSummaryDTO);

      when(customerRepository.findAll()).thenReturn(entities);
      when(customerMapper.toSummaryDTOList(entities)).thenReturn(summaryDTOs);

      // When
      List<CustomerSummaryDTO> result = customerService.getAllCustomers();

      // Then
      assertThat(result).hasSize(1);
      assertThat(result.getFirst()).isEqualTo(customerSummaryDTO);
      verify(customerRepository).findAll();
      verify(customerMapper).toSummaryDTOList(entities);
    }

    @Test
    @DisplayName("Should return paginated customers")
    void shouldReturnPaginatedCustomers() {
      // Given
      Pageable pageable = PageRequest.of(0, 10);
      Page<CustomerEntity> entityPage = new PageImpl<>(Collections.singletonList(customerEntity));

      when(customerRepository.findAll(pageable)).thenReturn(entityPage);
      when(customerMapper.toSummaryDTO(customerEntity)).thenReturn(customerSummaryDTO);

      // When
      Page<CustomerSummaryDTO> result = customerService.getAllCustomers(pageable);

      // Then
      assertThat(result.getContent()).hasSize(1);
      assertThat(result.getContent().getFirst()).isEqualTo(customerSummaryDTO);
      verify(customerRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Should return customer by ID")
    void shouldReturnCustomerById() {
      // Given
      when(customerRepository.findById(1L)).thenReturn(Optional.of(customerEntity));
      when(customerMapper.toResponseDTO(customerEntity)).thenReturn(customerResponseDTO);

      // When
      CustomerResponseDTO result = customerService.getCustomerById(1L);

      // Then
      assertThat(result).isEqualTo(customerResponseDTO);
      verify(customerRepository).findById(1L);
      verify(customerMapper).toResponseDTO(customerEntity);
    }

    @Test
    @DisplayName("Should throw exception when customer not found by ID")
    void shouldThrowExceptionWhenCustomerNotFoundById() {
      // Given
      when(customerRepository.findById(1L)).thenReturn(Optional.empty());

      // When & Then
      assertThatThrownBy(() -> customerService.getCustomerById(1L))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessage("Customer not found with id: 1");

      verify(customerRepository).findById(1L);
      verifyNoInteractions(customerMapper);
    }

    @Test
    @DisplayName("Should return customer with vehicles")
    void shouldReturnCustomerWithVehicles() {
      // Given
      when(customerRepository.findByIdWithVehicles(1L)).thenReturn(Optional.of(customerEntity));
      when(customerMapper.toResponseDTOWithVehicles(customerEntity)).thenReturn(
          customerResponseDTO);

      // When
      CustomerResponseDTO result = customerService.getCustomerByIdWithVehicles(1L);

      // Then
      assertThat(result).isEqualTo(customerResponseDTO);
      verify(customerRepository).findByIdWithVehicles(1L);
      verify(customerMapper).toResponseDTOWithVehicles(customerEntity);
    }

    @Test
    @DisplayName("Should return customer by email")
    void shouldReturnCustomerByEmail() {
      // Given
      String email = "john@example.com";
      when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customerEntity));
      when(customerMapper.toResponseDTO(customerEntity)).thenReturn(customerResponseDTO);

      // When
      CustomerResponseDTO result = customerService.getCustomerByEmail(email);

      // Then
      assertThat(result).isEqualTo(customerResponseDTO);
      verify(customerRepository).findByEmail(email);
      verify(customerMapper).toResponseDTO(customerEntity);
    }
  }

  @Nested
  @DisplayName("Create Operations")
  class CreateOperations {

    @Test
    @DisplayName("Should create customer successfully")
    void shouldCreateCustomerSuccessfully() {
      // Given
      when(customerRepository.existsByEmail(customerRequestDTO.email())).thenReturn(false);
      when(customerMapper.toEntity(customerRequestDTO)).thenReturn(customerEntity);
      when(customerRepository.save(customerEntity)).thenReturn(customerEntity);
      when(customerMapper.toResponseDTO(customerEntity)).thenReturn(customerResponseDTO);

      // When
      CustomerResponseDTO result = customerService.createCustomer(customerRequestDTO);

      // Then
      assertThat(result).isEqualTo(customerResponseDTO);
      verify(customerRepository).existsByEmail(customerRequestDTO.email());
      verify(customerMapper).toEntity(customerRequestDTO);
      verify(customerRepository).save(customerEntity);
      verify(customerMapper).toResponseDTO(customerEntity);
    }

    @Test
    @DisplayName("Should throw exception when customer email already exists")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
      // Given
      when(customerRepository.existsByEmail(customerRequestDTO.email())).thenReturn(true);

      // When & Then
      assertThatThrownBy(() -> customerService.createCustomer(customerRequestDTO))
          .isInstanceOf(ResourceAlreadyExistsException.class)
          .hasMessage("Customer already exists with email: john@example.com");

      verify(customerRepository).existsByEmail(customerRequestDTO.email());
      verifyNoMoreInteractions(customerRepository);
      verifyNoInteractions(customerMapper);
    }
  }

  @Nested
  @DisplayName("Update Operations")
  class UpdateOperations {

    @Test
    @DisplayName("Should update customer successfully")
    void shouldUpdateCustomerSuccessfully() {
      // Given
      CustomerRequestDTO updateRequest = new CustomerRequestDTO(
          "John Updated",
          "john@example.com", // Same email
          "987-654-3210",
          "456 New St"
      );

      when(customerRepository.findById(1L)).thenReturn(Optional.of(customerEntity));
      when(customerRepository.save(customerEntity)).thenReturn(customerEntity);
      when(customerMapper.toResponseDTO(customerEntity)).thenReturn(customerResponseDTO);

      // When
      CustomerResponseDTO result = customerService.updateCustomer(1L, updateRequest);

      // Then
      assertThat(result).isEqualTo(customerResponseDTO);
      verify(customerRepository).findById(1L);
      verify(customerMapper).updateEntityFromDTO(customerEntity, updateRequest);
      verify(customerRepository).save(customerEntity);
      verify(customerMapper).toResponseDTO(customerEntity);
    }

    @Test
    @DisplayName("Should throw exception when updating to existing email")
    void shouldThrowExceptionWhenUpdatingToExistingEmail() {
      // Given
      CustomerRequestDTO updateRequest = new CustomerRequestDTO(
          "John Updated",
          "different@example.com", // Different email
          "987-654-3210",
          "456 New St"
      );

      when(customerRepository.findById(1L)).thenReturn(Optional.of(customerEntity));
      when(customerRepository.existsByEmail("different@example.com")).thenReturn(true);

      // When & Then
      assertThatThrownBy(() -> customerService.updateCustomer(1L, updateRequest))
          .isInstanceOf(ResourceAlreadyExistsException.class)
          .hasMessage("Customer already exists with email: different@example.com");

      verify(customerRepository).findById(1L);
      verify(customerRepository).existsByEmail("different@example.com");
      verifyNoMoreInteractions(customerRepository);
    }

    @Test
    @DisplayName("Should throw exception when customer to update not found")
    void shouldThrowExceptionWhenCustomerToUpdateNotFound() {
      // Given
      when(customerRepository.findById(1L)).thenReturn(Optional.empty());

      // When & Then
      assertThatThrownBy(() -> customerService.updateCustomer(1L, customerRequestDTO))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessage("Customer not found with id: 1");

      verify(customerRepository).findById(1L);
      verifyNoMoreInteractions(customerRepository);
    }
  }

  @Nested
  @DisplayName("Delete Operations")
  class DeleteOperations {

    @Test
    @DisplayName("Should delete customer successfully")
    void shouldDeleteCustomerSuccessfully() {
      // Given
      when(customerRepository.findById(1L)).thenReturn(Optional.of(customerEntity));

      // When
      customerService.deleteCustomer(1L);

      // Then
      verify(customerRepository).findById(1L);
      verify(customerRepository).delete(customerEntity);
    }

    @Test
    @DisplayName("Should throw exception when customer to delete not found")
    void shouldThrowExceptionWhenCustomerToDeleteNotFound() {
      // Given
      when(customerRepository.findById(1L)).thenReturn(Optional.empty());

      // When & Then
      assertThatThrownBy(() -> customerService.deleteCustomer(1L))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessage("Customer not found with id: 1");

      verify(customerRepository).findById(1L);
      verify(customerRepository, never()).delete(any());
    }
  }

}
