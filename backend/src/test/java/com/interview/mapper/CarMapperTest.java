package com.interview.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.interview.dto.car.CarCreateRequestDTO;
import com.interview.dto.car.CarDTO;
import com.interview.entity.Car;
import com.interview.entity.Owner;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CarMapperTest {

  private CarMapper carMapper;

  @BeforeEach
  void setUp() {
    final CarMappingUtils mapperUtils = new CarMappingUtilsImpl();
    carMapper = new CarMapperImpl(mapperUtils);
  }

  @Test
  void toDto_mapsAllFieldsIncludingOwnerId() {
    final Owner owner =
        Owner.builder()
            .id(55L)
            .name("Bob Owner")
            .personalNumber("555555")
            .birthDate(Instant.parse("1975-05-05T00:00:00Z"))
            .address("55 Owner St")
            .build();

    final Car car =
        Car.builder().id(20L).model("Ford Mustang").vin("VIN555555").owner(owner).build();

    final CarDTO dto = carMapper.toDto(car);

    assertThat(dto.getId()).isEqualTo(20L);
    assertThat(dto.getModel()).isEqualTo("Ford Mustang");
    assertThat(dto.getVin()).isEqualTo("VIN555555");
    assertThat(dto.getOwnerId()).isEqualTo(55L);
  }

  @Test
  void toEntity_mapsAllFieldsFromCreateRequest() {
    final CarCreateRequestDTO request =
        CarCreateRequestDTO.builder().model("Audi A4").vin("VIN987654321").ownerId(99L).build();

    final Car car = carMapper.toEntity(request);

    assertThat(car.getId()).isNull();
    assertThat(car.getVersion()).isNull();
    assertThat(car.getModel()).isEqualTo("Audi A4");
    assertThat(car.getVin()).isEqualTo("VIN987654321");
    assertThat(car.getOwner()).isNull(); // owner is ignored in mapping
    assertThat(car.getVersion()).isNull(); // version is ignored in mapping
  }
}
