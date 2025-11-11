package com.interview.service;

import com.interview.dto.CarDto;
import com.interview.dto.CustomerDto;
import com.interview.exception.CarNotFoundException;
import com.interview.exception.CustomerNotFoundException;
import com.interview.mapper.CarMapper;
import com.interview.mapper.CustomerMapper;
import com.interview.model.Car;
import com.interview.model.Customer;
import com.interview.repository.CarRepository;
import com.interview.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CarRepository carRepository;
    private final CustomerMapper customerMapper;
    private final CarMapper carMapper;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, CarRepository carRepository, CustomerMapper customerMapper, CarMapper carMapper) {
        this.customerRepository = customerRepository;
        this.carRepository = carRepository;
        this.customerMapper = customerMapper;
        this.carMapper = carMapper;
    }

    @Transactional
    public CustomerDto createCustomer(CustomerDto customerDto) {
        Customer customer = customerMapper.toEntity(customerDto);

        if (customerDto.getCarIds() != null && !customerDto.getCarIds().isEmpty()) {
            Set<Car> cars = new HashSet<>(carRepository.findAllById(customerDto.getCarIds()));
            if (cars.size() != customerDto.getCarIds().size()) {
                throw new CarNotFoundException("One or more cars not found");
            }
            customer.setCars(cars);
            for (Car car : cars) {
                car.getCustomers().add(customer);
            }
        }
        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toDto(savedCustomer);
    }

    @Transactional(readOnly = true)
    public List<CustomerDto> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(customerMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CustomerDto getCustomerById(Long id) {
        return customerRepository.findById(id)
                .map(customerMapper::toDto)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id " + id));
    }

    @Transactional
    public CustomerDto updateCustomer(Long id, CustomerDto customerDto) {
        return customerRepository.findById(id).map(customer -> {
            customer.setFirstName(customerDto.getFirstName());
            customer.setLastName(customerDto.getLastName());
            customer.setEmail(customerDto.getEmail());

            if (customerDto.getCarIds() != null) {
                // Clear existing associations from owning side
                for (Car car : customer.getCars()) {
                    car.getCustomers().remove(customer);
                }
                customer.getCars().clear(); // clear inverse side

                // Add new associations
                if (!customerDto.getCarIds().isEmpty()) {
                    Set<Car> newCars = new HashSet<>(carRepository.findAllById(customerDto.getCarIds()));
                    if (newCars.size() != customerDto.getCarIds().size()) {
                        throw new CarNotFoundException("One or more cars not found");
                    }
                    for (Car car : newCars) {
                        customer.getCars().add(car);
                        car.getCustomers().add(customer);
                    }
                }
            }
            Customer updatedCustomer = customerRepository.save(customer);
            return customerMapper.toDto(updatedCustomer);
        }).orElseThrow(() -> new CustomerNotFoundException("Customer not found with id " + id));
    }

    @Transactional
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new CustomerNotFoundException("Customer not found with id " + id);
        }
        customerRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Set<CarDto> getCustomerCars(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id " + customerId));
        return customer.getCars().stream()
                .map(carMapper::toDto)
                .collect(Collectors.toSet());
    }
}
