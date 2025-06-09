package com.interview.service;

import com.interview.dto.car.CarCreateRequestDTO;
import com.interview.dto.car.CarDTO;

public interface CarService {

  CarDTO getCarById(final Long id);

  CarDTO createCar(final CarCreateRequestDTO request);

  CarDTO deleteCarById(final Long id);
}
