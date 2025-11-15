package com.interview.service;

import com.interview.repository.CarMakeRepository;
import org.springframework.stereotype.Service;

@Service
public class CarMakeService {

    private final CarMakeRepository carMakeRepository;

    public CarMakeService(CarMakeRepository carMakeRepository) {
        this.carMakeRepository = carMakeRepository;
    }
}
