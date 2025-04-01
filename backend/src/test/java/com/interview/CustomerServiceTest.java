package com.interview;

import com.interview.model.Customer;
import com.interview.model.CustomerDTO;
import com.interview.repository.CustomerRepository;
import com.interview.service.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @BeforeEach
    public void setUp() {
        // Initializes the mocks
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUpdateCustomer() {
        Long userId = 1L;

        Customer existingCustomer = new Customer();
        existingCustomer.setId(userId);
        existingCustomer.setEmail("test@gmail.com");
        existingCustomer.setFirstName("Mary");
        existingCustomer.setLastName("Cooper");
        existingCustomer.setAddress("100 Main Street, Boston, MA");

        CustomerDTO updatedCustomer = new CustomerDTO();
        updatedCustomer.setId(existingCustomer.getId());
        updatedCustomer.setEmail(existingCustomer.getEmail());
        updatedCustomer.setFirstName(existingCustomer.getFirstName());
        updatedCustomer.setLastName(existingCustomer.getLastName());
        updatedCustomer.setAddress("50 Main Street, Boston, MA");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(existingCustomer));

        existingCustomer.setAddress(updatedCustomer.getAddress());
        when(customerRepository.save(existingCustomer)).thenReturn(existingCustomer);

        CustomerDTO returnedUpdated = customerService.updateCustomer(1L, updatedCustomer);
        assertThat(returnedUpdated.getAddress()).isEqualTo(updatedCustomer.getAddress());

    }

    @Test
    public void testUpdateCustomerNotFound() {
        Long userId = 1L;

        CustomerDTO updatedCustomer = new CustomerDTO();
        updatedCustomer.setId(userId);
        updatedCustomer.setEmail("test@gmail.com");
        updatedCustomer.setFirstName("Mary");
        updatedCustomer.setLastName("Cooper");
        updatedCustomer.setAddress("50 Main Street, Boston, MA");

        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            customerService.updateCustomer(userId, updatedCustomer);  // Method that should throw the exception
        });

    }
}
