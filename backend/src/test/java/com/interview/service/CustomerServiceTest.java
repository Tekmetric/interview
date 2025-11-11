package com.interview.service;

import com.interview.dto.CarDto;
import com.interview.dto.CustomerDto;
import com.interview.exception.CustomerNotFoundException;
import com.interview.mapper.CarMapper;
import com.interview.mapper.CustomerMapper;
import com.interview.model.Car;
import com.interview.model.Customer;
import com.interview.repository.CarRepository;
import com.interview.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private CarMapper carMapper;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void createCustomer_shouldCreateCustomer() {
        CustomerDto customerDto = new CustomerDto(null, "John", "Doe", "john.doe@example.com", Collections.singleton(1L));
        Customer customer = new Customer("John", "Doe", "john.doe@example.com");
        Car car = new Car();
        car.setId(1L);
        car.setCustomers(new HashSet<>());

        when(customerMapper.toEntity(any(CustomerDto.class))).thenReturn(customer);
        when(carRepository.findAllById(any(Set.class))).thenReturn(Collections.singletonList(car));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(customerMapper.toDto(any(Customer.class))).thenReturn(customerDto);

        CustomerDto result = customerService.createCustomer(customerDto);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertTrue(car.getCustomers().contains(customer));
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void updateCustomer_shouldUpdateCustomer() {
        long customerId = 1L;
        CustomerDto customerDto = new CustomerDto(customerId, "John", "Doe", "john.doe@example.com", Collections.singleton(1L));
        Customer customer = new Customer("John", "Doe", "john.doe@example.com");
        customer.setId(customerId);
        customer.setCars(new HashSet<>());

        Car car = new Car();
        car.setId(1L);
        car.setCustomers(new HashSet<>());

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(carRepository.findAllById(any(Set.class))).thenReturn(Collections.singletonList(car));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(customerMapper.toDto(any(Customer.class))).thenReturn(customerDto);

        CustomerDto result = customerService.updateCustomer(customerId, customerDto);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertTrue(customer.getCars().contains(car));
        assertTrue(car.getCustomers().contains(customer));
        verify(customerRepository, times(1)).save(any(Customer.class));
    }


    @Test
    void getCustomerById_shouldReturnCustomer() {
        Customer customer = new Customer("John", "Doe", "john.doe@example.com");
        customer.setId(1L);
        CustomerDto customerDto = new CustomerDto(1L, "John", "Doe", "john.doe@example.com", Collections.emptySet());

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerMapper.toDto(any(Customer.class))).thenReturn(customerDto);

        CustomerDto result = customerService.getCustomerById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getCustomerById_shouldThrowNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(CustomerNotFoundException.class, () -> customerService.getCustomerById(1L));
    }

    @Test
    void getCustomerCars_shouldReturnCars() {
        Customer customer = new Customer("John", "Doe", "john.doe@example.com");
        customer.setId(1L);
        Car car = new Car("Toyota", "Camry", 2022, "White", "12345678901234567");
        customer.setCars(Collections.singleton(car));
        CarDto carDto = new CarDto(1L, "Toyota", "Camry", 2022, "White", "12345678901234567", Collections.emptySet());

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(carMapper.toDto(any(Car.class))).thenReturn(carDto);

        Set<CarDto> result = customerService.getCustomerCars(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getAllCustomers_shouldReturnAllCustomers() {
        Customer customer = new Customer("John", "Doe", "john.doe@example.com");
        CustomerDto customerDto = new CustomerDto(1L, "John", "Doe", "john.doe@example.com", Collections.emptySet());

        when(customerRepository.findAll()).thenReturn(Collections.singletonList(customer));
        when(customerMapper.toDto(any(Customer.class))).thenReturn(customerDto);

        var result = customerService.getAllCustomers();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void deleteCustomer_shouldDeleteCustomer() {
        long customerId = 1L;
        when(customerRepository.existsById(customerId)).thenReturn(true);
        doNothing().when(customerRepository).deleteById(customerId);

        customerService.deleteCustomer(customerId);

        verify(customerRepository, times(1)).deleteById(customerId);
    }
}
