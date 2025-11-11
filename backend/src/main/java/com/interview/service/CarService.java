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
public class CarService {

    private final CarRepository carRepository;
    private final CustomerRepository customerRepository;
    private final CarMapper carMapper;
    private final CustomerMapper customerMapper;

    @Autowired
    public CarService(CarRepository carRepository, CustomerRepository customerRepository, CarMapper carMapper, CustomerMapper customerMapper) {
        this.carRepository = carRepository;
        this.customerRepository = customerRepository;
        this.carMapper = carMapper;
        this.customerMapper = customerMapper;
    }

    @Transactional
    public CarDto createCar(CarDto carDto) {
        Car car = carMapper.toEntity(carDto);
        if (carDto.getCustomerIds() != null && !carDto.getCustomerIds().isEmpty()) {
            Set<Customer> customers = new HashSet<>(customerRepository.findAllById(carDto.getCustomerIds()));
            if (customers.size() != carDto.getCustomerIds().size()) {
                throw new CustomerNotFoundException("One or more customers not found");
            }
            car.setCustomers(customers);
        }
        Car savedCar = carRepository.save(car);
        return carMapper.toDto(savedCar);
    }

    @Transactional(readOnly = true)
    public List<CarDto> getAllCars() {
        return carRepository.findAll().stream()
                .map(carMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CarDto getCarById(Long id) {
        return carRepository.findById(id)
                .map(carMapper::toDto)
                .orElseThrow(() -> new CarNotFoundException("Car not found with id " + id));
    }

    @Transactional
    public CarDto updateCar(Long id, CarDto carDto) {
        return carRepository.findById(id).map(car -> {
            car.setMake(carDto.getMake());
            car.setModel(carDto.getModel());
            car.setModelYear(carDto.getModelYear());
            car.setColor(carDto.getColor());
            car.setVin(carDto.getVin());

            if (carDto.getCustomerIds() != null) {
                // Clear existing associations from owning side
                for (Customer customer : car.getCustomers()) {
                    customer.getCars().remove(car);
                }
                car.getCustomers().clear(); // clear inverse side

                // Add new associations
                if (!carDto.getCustomerIds().isEmpty()) {
                    Set<Customer> newCustomers = new HashSet<>(customerRepository.findAllById(carDto.getCustomerIds()));
                    if (newCustomers.size() != carDto.getCustomerIds().size()) {
                        throw new CustomerNotFoundException("One or more customers not found");
                    }
                    for (Customer customer : newCustomers) {
                        car.getCustomers().add(customer);
                        customer.getCars().add(car);
                    }
                }
            }

            Car updatedCar = carRepository.save(car);
            return carMapper.toDto(updatedCar);
        }).orElseThrow(() -> new CarNotFoundException("Car not found with id " + id));
    }

    @Transactional
    public void deleteCar(Long id) {
        if (!carRepository.existsById(id)) {
            throw new CarNotFoundException("Car not found with id " + id);
        }
        carRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Set<CustomerDto> getCarOwners(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new CarNotFoundException("Car not found with id " + carId));
        return car.getCustomers().stream()
                .map(customerMapper::toDto)
                .collect(Collectors.toSet());
    }

    @Transactional
    public void addOwnerToCar(Long carId, Long customerId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new CarNotFoundException("Car not found with id " + carId));
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id " + customerId));
        car.getCustomers().add(customer);
        carRepository.save(car);
    }
}
