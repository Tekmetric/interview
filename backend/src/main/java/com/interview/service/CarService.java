package com.interview.service;

import com.interview.exception.ResourceNotFoundException;
import com.interview.model.DtoMapper;
import com.interview.model.db.Car;
import com.interview.model.dto.JobCreateRequest;
import com.interview.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CarService {

    private final CarRepository carRepository;

    @Transactional
    public Car getOrCreateCar(JobCreateRequest request) {
        Car car;
        if (request.carId() != null) {
            // Obtain car from db
            car = carRepository.findById(request.carId()).orElseThrow(ResourceNotFoundException::new);
        } else {
            // Create a new car with the provided data
            car = DtoMapper.Instance.toCarEntity(request);
            car = carRepository.save(car);
        }
        return car;
    }
}
