package com.interview.tests;

import com.interview.dto.CustomerDTO;
import com.interview.entity.Customer;
import com.interview.mapper.CustomerMapper;
import com.interview.repository.CustomerRepository;
import com.interview.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService customerService;

    public CustomerServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCustomer() {
        Customer customer = new Customer();
        customer.setName("John Doe");
        customer.setEmail("john.doe@example.com");

        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setName("John Doe");
        customerDTO.setEmail("john.doe@example.com");
        when(customerMapper.toDTO(any(Customer.class))).thenReturn(customerDTO);
        when(customerMapper.toEntity(any(CustomerDTO.class))).thenReturn(customer);


        CustomerDTO createdCustomer = customerService.createCustomer(customerDTO);


        assertNotNull(createdCustomer);
        assertEquals("John Doe", createdCustomer.getName());
        assertEquals("john.doe@example.com", createdCustomer.getEmail());
    }

    @Test
    void testGetCustomerById() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("John Doe");
        customer.setEmail("john.doe@example.com");

        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(1L);
        customerDTO.setName("John Doe");
        customerDTO.setEmail("john.doe@example.com");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerMapper.toDTO(any(Customer.class))).thenReturn(customerDTO);


        CustomerDTO foundCustomer = customerService.getCustomerById(1L);

        assertNotNull(foundCustomer);
        assertEquals("John Doe", foundCustomer.getName());
        assertEquals("john.doe@example.com", foundCustomer.getEmail());
    }

    @Test
    void testGetCustomerById_NotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            customerService.getCustomerById(1L);
        });

        assertEquals("Customer not found with id 1", exception.getMessage());
    }
}
