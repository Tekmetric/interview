package com.interview.service;

import com.interview.dto.CustomerDTO;
import com.interview.entity.CustomerEntity;
import com.interview.exception.DuplicateResourceException;
import com.interview.exception.ResourceNotFoundException;
import com.interview.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerService Unit Tests")
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private CustomerDTO validCustomerDTO;
    private CustomerEntity customerEntity;

    @BeforeEach
    void setUp() {
        validCustomerDTO = new CustomerDTO();
        validCustomerDTO.setFirstName("John");
        validCustomerDTO.setLastName("Doe");
        validCustomerDTO.setEmail("john.doe@example.com");
        validCustomerDTO.setPhoneNumber("555-0101");

        customerEntity = new CustomerEntity();
        customerEntity.setId(1L);
        customerEntity.setFirstName("John");
        customerEntity.setLastName("Doe");
        customerEntity.setEmail("john.doe@example.com");
        customerEntity.setPhoneNumber("555-0101");
    }

    @Test
    @DisplayName("Should create customer successfully when email is unique")
    void shouldCreateCustomer_WhenEmailIsUnique() {
        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        when(customerRepository.save(any(CustomerEntity.class))).thenReturn(customerEntity);

        CustomerDTO result = customerService.createCustomer(validCustomerDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
        
        verify(customerRepository).existsByEmail("john.doe@example.com");
        verify(customerRepository).save(any(CustomerEntity.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when email already exists")
    void shouldThrowDuplicateResourceException_WhenEmailAlreadyExists() {
        when(customerRepository.existsByEmail(anyString())).thenReturn(true);

        assertThatThrownBy(() -> customerService.createCustomer(validCustomerDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("email")
                .hasMessageContaining("john.doe@example.com");

        verify(customerRepository).existsByEmail("john.doe@example.com");
        verify(customerRepository, never()).save(any(CustomerEntity.class));
    }

    @Test
    @DisplayName("Should get customer by ID successfully when customer exists")
    void shouldGetCustomerById_WhenCustomerExists() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customerEntity));

        CustomerDTO result = customerService.getCustomerById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
        
        verify(customerRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when customer ID not found")
    void shouldThrowResourceNotFoundException_WhenCustomerIdNotFound() {
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.getCustomerById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Customer")
                .hasMessageContaining("999");

        verify(customerRepository).findById(999L);
    }

    @Test
    @DisplayName("Should update customer successfully when email is unchanged")
    void shouldUpdateCustomer_WhenEmailIsUnchanged() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customerEntity));
        when(customerRepository.save(any(CustomerEntity.class))).thenReturn(customerEntity);

        validCustomerDTO.setFirstName("Jane");
        CustomerDTO result = customerService.updateCustomer(1L, validCustomerDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        
        verify(customerRepository).findById(1L);
        verify(customerRepository).save(any(CustomerEntity.class));
        verify(customerRepository, never()).existsByEmail(anyString());
    }

    @Test
    @DisplayName("Should update customer successfully when new email is unique")
    void shouldUpdateCustomer_WhenNewEmailIsUnique() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customerEntity));
        when(customerRepository.existsByEmail("newemail@example.com")).thenReturn(false);
        when(customerRepository.save(any(CustomerEntity.class))).thenReturn(customerEntity);

        validCustomerDTO.setEmail("newemail@example.com");
        CustomerDTO result = customerService.updateCustomer(1L, validCustomerDTO);

        assertThat(result).isNotNull();
        
        verify(customerRepository).findById(1L);
        verify(customerRepository).existsByEmail("newemail@example.com");
        verify(customerRepository).save(any(CustomerEntity.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when updating with existing email")
    void shouldThrowDuplicateResourceException_WhenUpdatingWithExistingEmail() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customerEntity));
        when(customerRepository.existsByEmail("existing@example.com")).thenReturn(true);

        validCustomerDTO.setEmail("existing@example.com");

        assertThatThrownBy(() -> customerService.updateCustomer(1L, validCustomerDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("email")
                .hasMessageContaining("existing@example.com");

        verify(customerRepository).findById(1L);
        verify(customerRepository).existsByEmail("existing@example.com");
        verify(customerRepository, never()).save(any(CustomerEntity.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent customer")
    void shouldThrowResourceNotFoundException_WhenUpdatingNonExistentCustomer() {
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.updateCustomer(999L, validCustomerDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Customer")
                .hasMessageContaining("999");

        verify(customerRepository).findById(999L);
        verify(customerRepository, never()).save(any(CustomerEntity.class));
    }

    @Test
    @DisplayName("Should delete customer successfully when customer exists")
    void shouldDeleteCustomer_WhenCustomerExists() {
        when(customerRepository.existsById(1L)).thenReturn(true);
        doNothing().when(customerRepository).deleteById(1L);

        assertThatCode(() -> customerService.deleteCustomer(1L))
                .doesNotThrowAnyException();

        verify(customerRepository).existsById(1L);
        verify(customerRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent customer")
    void shouldThrowResourceNotFoundException_WhenDeletingNonExistentCustomer() {
        when(customerRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> customerService.deleteCustomer(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Customer")
                .hasMessageContaining("999");

        verify(customerRepository).existsById(999L);
        verify(customerRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should get all customers without filters")
    void shouldGetAllCustomers_WithoutFilters() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<CustomerEntity> customerPage = new PageImpl<>(List.of(customerEntity));
        when(customerRepository.findAll(pageable)).thenReturn(customerPage);

        Page<CustomerDTO> result = customerService.getAllCustomers(null, null, null, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getEmail()).isEqualTo("john.doe@example.com");
        
        verify(customerRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Should filter customers by email")
    void shouldFilterCustomers_ByEmail() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<CustomerEntity> customerPage = new PageImpl<>(List.of(customerEntity));
        when(customerRepository.findByEmailContainingIgnoreCase("john", pageable)).thenReturn(customerPage);

        Page<CustomerDTO> result = customerService.getAllCustomers("john", null, null, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        
        verify(customerRepository).findByEmailContainingIgnoreCase("john", pageable);
        verify(customerRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Should filter customers by first name")
    void shouldFilterCustomers_ByFirstName() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<CustomerEntity> customerPage = new PageImpl<>(List.of(customerEntity));
        when(customerRepository.findByFirstNameContainingIgnoreCase("John", pageable)).thenReturn(customerPage);

        Page<CustomerDTO> result = customerService.getAllCustomers(null, "John", null, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        
        verify(customerRepository).findByFirstNameContainingIgnoreCase("John", pageable);
    }

    @Test
    @DisplayName("Should filter customers by last name")
    void shouldFilterCustomers_ByLastName() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<CustomerEntity> customerPage = new PageImpl<>(List.of(customerEntity));
        when(customerRepository.findByLastNameContainingIgnoreCase("Doe", pageable)).thenReturn(customerPage);

        Page<CustomerDTO> result = customerService.getAllCustomers(null, null, "Doe", pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        
        verify(customerRepository).findByLastNameContainingIgnoreCase("Doe", pageable);
    }

    @Test
    @DisplayName("Should prioritize email filter when multiple filters provided")
    void shouldPrioritizeEmailFilter_WhenMultipleFiltersProvided() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<CustomerEntity> customerPage = new PageImpl<>(List.of(customerEntity));
        when(customerRepository.findByEmailContainingIgnoreCase("john", pageable)).thenReturn(customerPage);

        Page<CustomerDTO> result = customerService.getAllCustomers("john", "Jane", "Smith", pageable);

        verify(customerRepository).findByEmailContainingIgnoreCase("john", pageable);
        verify(customerRepository, never()).findByFirstNameContainingIgnoreCase(anyString(), any());
        verify(customerRepository, never()).findByLastNameContainingIgnoreCase(anyString(), any());
    }
}
