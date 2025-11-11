package com.interview.service;

import com.interview.dto.CarDto;
import com.interview.dto.CustomerDto;
import com.interview.exception.CarNotFoundException;
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
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CarMapper carMapper;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CarService carService;

    @Test
    void createCar_shouldCreateCar() {
        CarDto carDto = new CarDto(null, "Toyota", "Camry", 2022, "White", "12345678901234567", Collections.emptySet());
        Car car = new Car("Toyota", "Camry", 2022, "White", "12345678901234567");

        when(carMapper.toEntity(any(CarDto.class))).thenReturn(car);
        when(carRepository.save(any(Car.class))).thenReturn(car);
        when(carMapper.toDto(any(Car.class))).thenReturn(carDto);

        CarDto result = carService.createCar(carDto);

        assertNotNull(result);
        assertEquals("Toyota", result.getMake());
        verify(carRepository, times(1)).save(any(Car.class));
    }

    @Test
    void getCarById_shouldReturnCar() {
        Car car = new Car("Toyota", "Camry", 2022, "White", "12345678901234567");
        car.setId(1L);
        CarDto carDto = new CarDto(1L, "Toyota", "Camry", 2022, "White", "12345678901234567", Collections.emptySet());

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(carMapper.toDto(any(Car.class))).thenReturn(carDto);

        CarDto result = carService.getCarById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getCarById_shouldThrowNotFound() {
        when(carRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(CarNotFoundException.class, () -> carService.getCarById(1L));
    }

    @Test
    void getCarOwners_shouldReturnCustomers() {
        Car car = new Car("Toyota", "Camry", 2022, "White", "12345678901234567");
        car.setId(1L);
        Customer customer = new Customer("John", "Doe", "john.doe@example.com");
        car.setCustomers(Collections.singleton(customer));
        CustomerDto customerDto = new CustomerDto(1L, "John", "Doe", "john.doe@example.com", Collections.emptySet());

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(customerMapper.toDto(any(Customer.class))).thenReturn(customerDto);

        Set<CustomerDto> result = carService.getCarOwners(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void addOwnerToCar_shouldAddOwner() {
        Car car = new Car("Toyota", "Camry", 2022, "White", "12345678901234567");
        car.setId(1L);
        Customer customer = new Customer("John", "Doe", "john.doe@example.com");
        customer.setId(1L);

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        carService.addOwnerToCar(1L, 1L);

        verify(carRepository, times(1)).save(any(Car.class));
    }
}
