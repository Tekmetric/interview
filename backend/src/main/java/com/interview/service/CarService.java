package com.interview.service;

import com.interview.dto.car.CarCreateRequestDTO;
import com.interview.dto.car.CarDTO;
import com.interview.dto.car.CarUpdateRequestDTO;
import com.interview.dto.page.PageResponseDTO;
import org.springframework.data.domain.Pageable;

public interface CarService {

  CarDTO getCarById(final Long id);

  CarDTO createCar(final CarCreateRequestDTO request);

  CarDTO deleteCarById(final Long id);

  PageResponseDTO<CarDTO> getCars(final String query, final Pageable pageable);

  CarDTO updateCar(final Long id, final CarUpdateRequestDTO request);
}
