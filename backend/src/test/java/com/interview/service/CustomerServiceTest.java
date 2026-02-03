package com.interview.service;

import com.interview.exception.ResourceAlreadyExistsException;
import com.interview.exception.ResourceNotFoundException;
import com.interview.mapper.CustomerMapper;
import com.interview.model.dto.CustomerDTO;
import com.interview.model.entity.Customer;
import com.interview.repository.CustomerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CustomerServiceTest {
    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;
    private CustomerDTO customerDto;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .id(1L)
                .firstName("Jack")
                .lastName("McGinnis")
                .phone("1234567890")
                .build();

        customerDto = CustomerDTO.builder()
                .id(1L)
                .firstName("Jack")
                .lastName("McGinnis")
                .phone("1234567890")
                .build();
    }

    @Test
    public void save_Success() {
        when(customerRepository.existsByPhone(customerDto.getPhone())).thenReturn(false);
        when(customerMapper.toEntity(any(CustomerDTO.class))).thenReturn(customer);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(customerMapper.toDto(any(Customer.class))).thenReturn(customerDto);

        CustomerDTO savedDto = customerService.createCustomer(customerDto);

        Assertions.assertNotNull(savedDto);
        Assertions.assertEquals("Jack", savedDto.getFirstName());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    public void save_ThrowsException_IfPhoneExists() {
        when(customerRepository.existsByPhone(customerDto.getPhone())).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class,
                () -> customerService.createCustomer(customerDto));
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    public void findById_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerMapper.toDto(customer)).thenReturn(customerDto);

        CustomerDTO result = customerService.getCustomerById(1L);

        Assertions.assertEquals(1L, result.getId());
        verify(customerRepository).findById(1L);
    }

    @Test
    public void findById_NotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> customerService.getCustomerById(1L));
    }

    @Test
    public void update_Partial() {
        CustomerDTO updateDto = CustomerDTO.builder().firstName("Marisa").build();
        doAnswer(invocation -> {
            CustomerDTO dto = invocation.getArgument(0);
            Customer entity = invocation.getArgument(1);
            if (dto.getFirstName() != null) entity.setFirstName(dto.getFirstName());
            return null;
        }).when(customerMapper).updateEntityFromDto(any(CustomerDTO.class), any(Customer.class));

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(customerMapper.toDto(any(Customer.class))).thenReturn(customerDto);

        customerService.updateCustomer(1L, updateDto);

        Assertions.assertEquals("Marisa", customer.getFirstName());
        Assertions.assertEquals("McGinnis", customer.getLastName());
        verify(customerRepository).save(customer);
    }

    @Test
    public void delete_Success() {
        Long customerId = 1L;
        when(customerRepository.existsById(customerId)).thenReturn(true);

        customerService.deleteCustomer(customerId);
        verify(customerRepository, times(1)).deleteById(customerId);
    }

    @Test
    public void delete_ThrowsException_IfNotFound() {
        Long customerId = 99L;
        when(customerRepository.existsById(customerId)).thenReturn(false);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            customerService.deleteCustomer(customerId);
        });
        verify(customerRepository, never()).deleteById(anyLong());
    }
}
