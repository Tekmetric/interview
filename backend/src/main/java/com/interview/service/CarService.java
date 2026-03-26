package com.interview.service;

import com.interview.dto.CarRequest;
import com.interview.dto.CarResponse;
import com.interview.exception.CarNotFoundException;
import com.interview.exception.DuplicateVinException;
import com.interview.mapper.CarMapper;
import com.interview.model.Car;
import com.interview.model.CarStatus;
import com.interview.repository.CarRepository;
import com.interview.repository.CarSpecification;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final CarMapper carMapper;
    private final MeterRegistry meterRegistry;

    private Counter carsCreatedCounter;

    @PostConstruct
    private void initMetrics() {
        carsCreatedCounter = Counter.builder("cars_created_total")
                .description("Total number of car entities successfully created")
                .tag("application", "carshop")
                .register(meterRegistry);
    }

    @Transactional(readOnly = true)
    public CarResponse getById(Long id) {
        log.info("Fetching car with id={}", id);
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new CarNotFoundException(id));
        return carMapper.toResponse(car);
    }

    @Transactional(readOnly = true)
    public Page<CarResponse> getAll(CarStatus status, String brand,
                                     BigDecimal minPrice, BigDecimal maxPrice,
                                     Pageable pageable) {
        log.info("Fetching cars page={} size={} status={} brand={} minPrice={} maxPrice={}",
                pageable.getPageNumber(), pageable.getPageSize(), status, brand, minPrice, maxPrice);
        Specification<Car> spec = Specification
                .where(CarSpecification.hasStatus(status))
                .and(CarSpecification.hasBrand(brand))
                .and(CarSpecification.hasBasePriceMin(minPrice))
                .and(CarSpecification.hasBasePriceMax(maxPrice));

        Page<Car> carPage = carRepository.findAll(spec, pageable);
        return carMapper.toResponsePage(carPage);
    }

    public CarResponse create(CarRequest request) {
        log.info("Creating car with vin={} brand={} model={}", request.vin(), request.brand(), request.model());
        if (carRepository.existsByVin(request.vin())) {
            throw new DuplicateVinException(request.vin());
        }

        Car car = carMapper.toEntity(request);
        Car savedCar = carRepository.save(car);
        carsCreatedCounter.increment();
        log.info("Car created successfully id={} vin={}", savedCar.getId(), savedCar.getVin());
        return carMapper.toResponse(savedCar);
    }

    public CarResponse update(Long id, CarRequest request) {
        log.info("Updating car with id={} vin={}", id, request.vin());
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new CarNotFoundException(id));

        if (!car.getVin().equals(request.vin()) && carRepository.existsByVinAndIdNot(request.vin(), id)) {
            throw new DuplicateVinException(request.vin());
        }

        carMapper.updateEntity(car, request);
        Car updatedCar = carRepository.save(car);
        log.info("Car updated successfully id={}", updatedCar.getId());
        return carMapper.toResponse(updatedCar);
    }

    public void delete(Long id) {
        log.info("Deleting car with id={}", id);
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new CarNotFoundException(id));
        carRepository.delete(car);
        log.info("Car deleted successfully id={}", id);
    }
}
