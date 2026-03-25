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
import com.interview.validator.CarRequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Transactional(readOnly = true)
    public CarResponse getById(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new CarNotFoundException(id));
        return carMapper.toResponse(car);
    }

    @Transactional(readOnly = true)
    public Page<CarResponse> getAll(CarStatus status, String brand,
                                     BigDecimal minPrice, BigDecimal maxPrice,
                                     Pageable pageable) {
        Specification<Car> spec = Specification
                .where(CarSpecification.hasStatus(status))
                .and(CarSpecification.hasBrand(brand))
                .and(CarSpecification.hasBasePriceMin(minPrice))
                .and(CarSpecification.hasBasePriceMax(maxPrice));

        Page<Car> carPage = carRepository.findAll(spec, pageable);
        return carMapper.toResponsePage(carPage);
    }

    public CarResponse create(CarRequest request) {
        if (carRepository.existsByVin(request.vin())) {
            throw new DuplicateVinException(request.vin());
        }

        Car car = carMapper.toEntity(request);
        Car savedCar = carRepository.save(car);
        return carMapper.toResponse(savedCar);
    }

    public CarResponse update(Long id, CarRequest request) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new CarNotFoundException(id));

        if (!car.getVin().equals(request.vin()) && carRepository.existsByVinAndIdNot(request.vin(), id)) {
            throw new DuplicateVinException(request.vin());
        }

        carMapper.updateEntity(car, request);
        Car updatedCar = carRepository.save(car);
        return carMapper.toResponse(updatedCar);
    }

    public void delete(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new CarNotFoundException(id));
        carRepository.delete(car);
    }
}
