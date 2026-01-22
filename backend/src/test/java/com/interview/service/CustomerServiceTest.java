package com.interview.service;

import com.interview.dto.CustomerDTO;
import com.interview.model.Customer;
import com.interview.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;
    private CustomerDTO customerDTO;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john.doe@example.com");

        customerDTO = new CustomerDTO();
        customerDTO.setId(1L);
        customerDTO.setFirstName("John");
        customerDTO.setLastName("Doe");
        customerDTO.setEmail("john.doe@example.com");
    }

    @Test
    void testFindAll() {
        // Arrange
        when(customerRepository.findAll()).thenReturn(Collections.singletonList(customer));

        // Act
        List<CustomerDTO> result = customerService.findAll();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getEmail()).isEqualTo(customer.getEmail());
        verify(customerRepository).findAll();
    }

    @Test
    void testFindOne_whenCustomerExists() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        // Act
        CustomerDTO result = customerService.findOne(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(customer.getId());
        verify(customerRepository).findById(1L);
    }

    @Test
    void testFindOne_whenCustomerDoesNotExist_shouldThrowException() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            customerService.findOne(1L);
        });
        verify(customerRepository).findById(1L);
    }

    @Test
    void testSave() {
        // Arrange
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // Act
        CustomerDTO result = customerService.save(customerDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(customer.getId());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void testPartialUpdate_whenCustomerExists() {
        // Arrange
        CustomerDTO updateDto = new CustomerDTO();
        updateDto.setId(1L);
        updateDto.setFirstName("Johnny");
        
        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(1L);
        updatedCustomer.setFirstName("Johnny");
        updatedCustomer.setLastName("Doe");
        updatedCustomer.setEmail("john.doe@example.com");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);

        // Act
        CustomerDTO result = customerService.partialUpdate(updateDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Johnny");
        assertThat(result.getLastName()).isEqualTo("Doe"); // Should remain unchanged
        verify(customerRepository).findById(1L);
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void testPartialUpdate_whenCustomerDoesNotExist_shouldThrowException() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            customerService.partialUpdate(customerDTO);
        });
        verify(customerRepository).findById(1L);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testDelete() {
        // Arrange
        doNothing().when(customerRepository).deleteById(1L);

        // Act
        customerService.delete(1L);

        // Assert
        verify(customerRepository, times(1)).deleteById(1L);
    }
}
