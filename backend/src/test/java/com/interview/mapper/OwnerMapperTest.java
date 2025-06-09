package com.interview.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.interview.dto.owner.OwnerCreateRequestDTO;
import com.interview.dto.owner.OwnerDTO;
import com.interview.dto.owner.OwnerUpdateRequestDTO;
import com.interview.entity.Car;
import com.interview.entity.Owner;
import java.time.Instant;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OwnerMapperTest {

  private OwnerMapper ownerMapper;

  @BeforeEach
  void setUp() {
    final CarMappingUtils mapperUtils = new CarMappingUtilsImpl();
    ownerMapper = new OwnerMapperImpl(mapperUtils);
  }

  @Test
  void toDto_mapsAllFields() {

    final Car car1 = Car.builder().id(100L).model("Tesla Model 3").vin("VIN100").build();
    final Car car2 = Car.builder().id(101L).model("BMW i3").vin("VIN101").build();

    final Owner owner =
        Owner.builder()
            .id(1L)
            .name("John Doe")
            .personalNumber("123456")
            .birthDate(Instant.parse("1990-01-01T00:00:00Z"))
            .address("123 Main St")
            .cars(Set.of(car1, car2))
            .build();

    final OwnerDTO dto = ownerMapper.toDto(owner);

    assertThat(dto.getId()).isEqualTo(1L);
    assertThat(dto.getName()).isEqualTo("John Doe");
    assertThat(dto.getPersonalNumber()).isEqualTo("123456");
    assertThat(dto.getBirthDate()).isEqualTo(Instant.parse("1990-01-01T00:00:00Z"));
    assertThat(dto.getAddress()).isEqualTo("123 Main St");
    assertThat(dto.getCars()).isNotNull();
    assertThat(dto.getCars()).hasSize(2);
    assertThat(dto.getCars()).extracting("id").containsExactlyInAnyOrder(100L, 101L);
    assertThat(dto.getCars())
        .extracting("model")
        .containsExactlyInAnyOrder("Tesla Model 3", "BMW i3");
    assertThat(dto.getCars()).extracting("vin").containsExactlyInAnyOrder("VIN100", "VIN101");
  }

  @Test
  void toEntity_mapsAllFields() {
    final OwnerCreateRequestDTO request =
        OwnerCreateRequestDTO.builder()
            .name("Jane Smith")
            .personalNumber("654321")
            .birthDate(Instant.parse("1985-05-20T00:00:00Z"))
            .address("456 Elm St")
            .build();

    final Owner owner = ownerMapper.toEntity(request);

    assertThat(owner.getName()).isEqualTo("Jane Smith");
    assertThat(owner.getPersonalNumber()).isEqualTo("654321");
    assertThat(owner.getBirthDate()).isEqualTo(Instant.parse("1985-05-20T00:00:00Z"));
    assertThat(owner.getAddress()).isEqualTo("456 Elm St");
    assertThat(owner.getId()).isNull();
    assertThat(owner.getVersion()).isNull();
    assertThat(owner.getCreatedAt()).isNull();
    assertThat(owner.getUpdatedAt()).isNull();
    assertThat(owner.getCars()).isNotNull();
    assertThat(owner.getCars()).isEmpty();
  }

  @Test
  void updateOwnerFromDto_updatesOnlyNonNullFields() {
    final OwnerUpdateRequestDTO update =
        OwnerUpdateRequestDTO.builder()
            .name("Updated Name")
            .personalNumber(null)
            .birthDate(Instant.parse("2000-01-01T00:00:00Z"))
            .address("Updated Address")
            .build();

    final Owner owner =
        Owner.builder()
            .name("Old Name")
            .personalNumber("999999")
            .birthDate(Instant.parse("1999-01-01T00:00:00Z"))
            .address("Old Address")
            .build();

    ownerMapper.updateOwnerFromDto(update, owner);

    assertThat(owner.getName()).isEqualTo("Updated Name");
    assertThat(owner.getPersonalNumber()).isEqualTo("999999");
    assertThat(owner.getBirthDate()).isEqualTo(Instant.parse("2000-01-01T00:00:00Z"));
    assertThat(owner.getAddress()).isEqualTo("Updated Address");
  }
}
