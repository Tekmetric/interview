package com.interview.service;

import static org.assertj.core.api.Assertions.*;

import com.interview.dto.car.CarCreateRequestDTO;
import com.interview.dto.car.CarDTO;
import com.interview.dto.owner.OwnerCreateRequestDTO;
import com.interview.dto.owner.OwnerDTO;
import com.interview.dto.page.PageResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class CarServiceTest {

  private static final EasyRandom easyRandom = new EasyRandom();

  @Autowired private CarService carService;

  @Autowired private OwnerService ownerService;

  private OwnerDTO owner;

  @BeforeEach
  void setUp() {
    final OwnerCreateRequestDTO ownerRequest = easyRandom.nextObject(OwnerCreateRequestDTO.class);
    owner = ownerService.createOwner(ownerRequest);
  }

  @Test
  void createCarAndGetCarById() {
    final CarCreateRequestDTO request = easyRandom.nextObject(CarCreateRequestDTO.class);
    request.setOwnerId(owner.getId());

    final CarDTO carDTO = carService.createCar(request);
    assertThat(carDTO.getModel()).isEqualTo(request.getModel());
    assertThat(carDTO.getVin()).isEqualTo(request.getVin());
    assertThat(carDTO.getOwnerId()).isEqualTo(owner.getId());

    final CarDTO found = carService.getCarById(carDTO.getId());
    assertThat(found.getModel()).isEqualTo(request.getModel());
    assertThat(found.getVin()).isEqualTo(request.getVin());
    assertThat(found.getOwnerId()).isEqualTo(owner.getId());
  }

  @Test
  void deleteCarByIdRemovesCar() {
    final CarCreateRequestDTO request = easyRandom.nextObject(CarCreateRequestDTO.class);
    request.setOwnerId(owner.getId());

    final CarDTO carDTO = carService.createCar(request);
    carService.deleteCarById(carDTO.getId());

    assertThatThrownBy(() -> carService.getCarById(carDTO.getId()))
        .isInstanceOf(EntityNotFoundException.class);
  }

  @Test
  void getCarsReturnsPagedResults() {
    for (int i = 0; i < 5; i++) {
      final CarCreateRequestDTO request = easyRandom.nextObject(CarCreateRequestDTO.class);
      request.setOwnerId(owner.getId());
      carService.createCar(request);
    }
    final PageResponseDTO<CarDTO> page = carService.getCars("", PageRequest.of(0, 3));
    assertThat(page.getContent()).hasSize(3);
    assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(5);
  }
}
