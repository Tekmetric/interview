package com.tekmetric.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.tekmetric.ValidationException;
import com.tekmetric.model.CarModel;
import com.tekmetric.request.CarCreationRequest;
import com.tekmetric.response.PagedResponse;
import com.tekmetric.response.car.CarResponse;
import java.time.Year;
import java.util.List;
import java.util.UUID;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;

class CarMapperTest {

  @Test
  void toResponse_mapsFields() {
    UUID id = UUID.randomUUID();

    CarModel model =
        CarModel.builder()
            .id(id)
            .make("Toyota")
            .model("RAV4")
            .manufactureYear(Year.of(2020))
            .color("red")
            .build();

    CarResponse response = CarMapper.toResponse(model);

    assertEquals(id, response.getId());
    assertTrue(response.getCarInfo().contains("Toyota"));
    assertTrue(response.getCarInfo().contains("RAV4"));
  }

  @Test
  void toPagedResponse_mapsPageMetadata() {
    UUID id = UUID.randomUUID();
    CarModel model =
        CarModel.builder()
            .id(id)
            .make("Toyota")
            .model("RAV4")
            .manufactureYear(Year.of(2020))
            .color("red")
            .build();

    Pageable pageable = PageRequest.of(1, 5);
    Page<CarModel> page = new PageImpl<>(List.of(model), pageable, 10);

    PagedResponse<CarResponse> response = CarMapper.toPagedResponse(page);

    assertEquals(1, response.getPage());
    assertEquals(5, response.getSize());
    assertEquals(10, response.getTotalElements());
    assertEquals(2, response.getTotalPages());
    assertEquals(1, response.getContent().size());
  }

  @Test
  void toModel_validRequest_usesValidationUtil() throws BadRequestException {
    CarCreationRequest request =
        CarCreationRequest.builder()
            .make("Toyota")
            .model("RAV4")
            .manufactureYear("2020")
            .color("red")
            .build();

    CarModel model = CarMapper.toModel(request);

    assertEquals("Toyota", model.getMake());
    assertEquals("RAV4", model.getModel());
    assertEquals(Year.of(2020), model.getManufactureYear());
    assertEquals("red", model.getColor());
  }

  @Test
  void toModel_invalidYear_throwsValidationException() {
    CarCreationRequest request =
        CarCreationRequest.builder()
            .make("Toyota")
            .model("RAV4")
            .manufactureYear("20") // invalid year
            .color("red")
            .build();

    assertThrows(ValidationException.class, () -> CarMapper.toModel(request));
  }
}
