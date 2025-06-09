package com.interview.resource;

import com.interview.api.CarApi;
import com.interview.dto.car.CarCreateRequestDTO;
import com.interview.dto.car.CarDTO;
import com.interview.dto.car.CarUpdateRequestDTO;
import com.interview.dto.page.PageResponseDTO;
import com.interview.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CarResource implements CarApi {

  private final CarService carService;

  @Override
  public ResponseEntity<CarDTO> getCarById(final Long id) {
    final CarDTO result = carService.getCarById(id);
    return ResponseEntity.ok(result);
  }

  @Override
  public ResponseEntity<PageResponseDTO<CarDTO>> getCars(
      final int page, final int size, final String query) {
    final PageResponseDTO<CarDTO> result = carService.getCars(query, PageRequest.of(page, size));
    return ResponseEntity.ok(result);
  }

  @Override
  public ResponseEntity<CarDTO> createCar(final CarCreateRequestDTO request) {
    final CarDTO result = carService.createCar(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(result);
  }

  @Override
  public ResponseEntity<CarDTO> deleteCarById(final Long id) {
    final CarDTO result = carService.deleteCarById(id);
    return ResponseEntity.status(HttpStatus.CREATED).body(result);
  }

  @Override
  public ResponseEntity<CarDTO> updateCar(final Long id, final CarUpdateRequestDTO request) {
    return null;
  }
}
