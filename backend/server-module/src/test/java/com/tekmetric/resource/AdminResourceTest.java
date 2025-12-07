package com.tekmetric.resource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.tekmetric.CarPortal;
import com.tekmetric.model.CarModel;
import com.tekmetric.request.CarBulkCreationRequest;
import com.tekmetric.request.CarCreationRequest;
import com.tekmetric.response.car.CarBulkCreationResponse;
import java.time.Year;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class AdminResourceTest {

  @Mock private CarPortal carPortal;

  @InjectMocks private AdminResource adminResource;

  @Test
  void createCars_allSuccess_returnsCreatedStatus() {
    CarCreationRequest req1 =
        CarCreationRequest.builder().make("Toyota").model("RAV4").manufactureYear("2020").build();
    CarCreationRequest req2 =
        CarCreationRequest.builder().make("Honda").model("CR-V").manufactureYear("2021").build();

    CarBulkCreationRequest bulk =
        CarBulkCreationRequest.builder().cars(List.of(req1, req2)).build();

    CarModel car1 =
        CarModel.builder()
            .id(UUID.randomUUID())
            .make("Toyota")
            .model("RAV4")
            .manufactureYear(Year.of(2020))
            .build();
    CarModel car2 =
        CarModel.builder()
            .id(UUID.randomUUID())
            .make("Honda")
            .model("CR-V")
            .manufactureYear(Year.of(2021))
            .build();

    when(carPortal.createCar(any())).thenReturn(car1).thenReturn(car2);

    ResponseEntity<CarBulkCreationResponse> response = adminResource.createCars(bulk);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(2, response.getBody().getSuccessCount());
    assertEquals(0, response.getBody().getFailureCount());
  }

  @Test
  void createCars_withFailures_returnsMultiStatus() {
    CarCreationRequest good =
        CarCreationRequest.builder().make("Toyota").model("RAV4").manufactureYear("2020").build();
    CarCreationRequest bad =
        CarCreationRequest.builder().make("BadMake").model("X").manufactureYear("2020").build();

    CarBulkCreationRequest bulk = CarBulkCreationRequest.builder().cars(List.of(good, bad)).build();

    CarModel car1 =
        CarModel.builder()
            .id(UUID.randomUUID())
            .make("Toyota")
            .model("RAV4")
            .manufactureYear(Year.of(2020))
            .build();

    when(carPortal.createCar(any()))
        .thenReturn(car1) // first item succeeds
        .thenThrow(new RuntimeException("Invalid make")); // second item fails

    ResponseEntity<CarBulkCreationResponse> response = adminResource.createCars(bulk);

    assertEquals(HttpStatus.MULTI_STATUS, response.getStatusCode());
    CarBulkCreationResponse body = response.getBody();
    assertNotNull(body);
    assertEquals(1, body.getSuccessCount());
    assertEquals(1, body.getFailureCount());
    assertEquals(1, body.getFailures().get(0).getIndex()); // second item failed
  }
}
