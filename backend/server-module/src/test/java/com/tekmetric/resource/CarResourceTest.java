package com.tekmetric.resource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.tekmetric.CarPortal;
import com.tekmetric.UserModel;
import com.tekmetric.ValidationException;
import com.tekmetric.mapper.CarMapper;
import com.tekmetric.model.CarFilter;
import com.tekmetric.model.CarModel;
import com.tekmetric.model.CarUpdates;
import com.tekmetric.request.CarCreationRequest;
import com.tekmetric.request.CarUpdateRequest;
import com.tekmetric.response.PagedResponse;
import com.tekmetric.response.car.CarResponse;
import com.tekmetric.util.ValidationUtil;
import java.time.LocalDate;
import java.time.Year;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class CarResourceTest {

  @Mock private CarPortal carPortal;

  @InjectMocks private CarResource carResource;

  @Test
  void getCarById_returnsMappedResponseAndOkStatus() {
    UUID id = UUID.randomUUID();

    UserModel owner =
        UserModel.builder()
            .id(UUID.randomUUID())
            .firstName("John")
            .lastName("Doe")
            .email("john.doe@example.com")
            .birthDate(LocalDate.of(1990, 1, 1))
            .build();

    CarModel carModel =
        CarModel.builder()
            .id(id)
            .owner(owner)
            .make("Toyota")
            .model("Corolla")
            .manufactureYear(Year.of(2020))
            .color("Blue")
            .build();

    when(carPortal.getById(id)).thenReturn(carModel);

    ResponseEntity<CarResponse> response = carResource.getCarById(id);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    CarResponse body = response.getBody();
    assertNotNull(body);

    // CarMapper.toResponse should use getCarInfo() and getUserInfo()
    assertEquals(id, body.getId());
    assertEquals(carModel.getCarInfo(), body.getCarInfo());
    assertEquals(carModel.getUserInfo(), body.getOwnerInfo());

    verify(carPortal).getById(id);
  }

  @Test
  void getCars_withValidYear_callsValidationAndReturnsPagedResponse() throws Exception {
    Pageable pageable = PageRequest.of(0, 10);

    CarFilter filter = new CarFilter();
    filter.setYear(2020);

    UserModel owner =
        UserModel.builder().id(UUID.randomUUID()).firstName("Alice").lastName("Smith").build();

    CarModel carModel =
        CarModel.builder()
            .id(UUID.randomUUID())
            .owner(owner)
            .make("Honda")
            .model("Civic")
            .manufactureYear(Year.of(2020))
            .color("Red")
            .build();

    Page<CarModel> carPage = new PageImpl<>(List.of(carModel), pageable, 1);

    when(carPortal.getCars(filter, pageable)).thenReturn(carPage);

    try (MockedStatic<ValidationUtil> validationMock = Mockito.mockStatic(ValidationUtil.class)) {

      ResponseEntity<PagedResponse<CarResponse>> response = carResource.getCars(filter, pageable);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      PagedResponse<CarResponse> body = response.getBody();
      assertNotNull(body);

      // verify validation was called
      validationMock.verify(() -> ValidationUtil.validateYearFormat("2020"));

      // verify paging metadata
      assertEquals(0, body.getPage());
      assertEquals(10, body.getSize());
      assertEquals(1L, body.getTotalElements());
      assertEquals(1, body.getTotalPages());
      assertTrue(body.isFirst());
      assertTrue(body.isLast());

      // verify content mapping
      assertNotNull(body.getContent());
      assertEquals(1, body.getContent().size());

      CarResponse first = body.getContent().get(0);
      assertEquals(carModel.getId(), first.getId());
      assertEquals(carModel.getCarInfo(), first.getCarInfo());
      assertEquals(carModel.getUserInfo(), first.getOwnerInfo());

      verify(carPortal).getCars(filter, pageable);
    }
  }

  @Test
  void getCars_withNullFilter_skipsValidationAndReturnsPagedResponse() throws Exception {
    Pageable pageable = PageRequest.of(0, 10);

    Page<CarModel> carPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

    when(carPortal.getCars(null, pageable)).thenReturn(carPage);

    try (MockedStatic<ValidationUtil> validationMock = Mockito.mockStatic(ValidationUtil.class)) {

      ResponseEntity<PagedResponse<CarResponse>> response = carResource.getCars(null, pageable);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      PagedResponse<CarResponse> body = response.getBody();
      assertNotNull(body);
      assertTrue(body.getContent().isEmpty());

      // ensure no validation call when filter is null
      validationMock.verifyNoInteractions();

      verify(carPortal).getCars(null, pageable);
    }
  }

  @Test
  void getCars_withInvalidYear_propagatesValidationException() throws Exception {
    Pageable pageable = PageRequest.of(0, 10);

    CarFilter filter = new CarFilter();
    filter.setYear(9999);

    try (MockedStatic<ValidationUtil> validationMock = Mockito.mockStatic(ValidationUtil.class)) {

      validationMock
          .when(() -> ValidationUtil.validateYearFormat("9999"))
          .thenThrow(new ValidationException(""));

      assertThrows(ValidationException.class, () -> carResource.getCars(filter, pageable));

      validationMock.verify(() -> ValidationUtil.validateYearFormat("9999"));
      verifyNoInteractions(carPortal);
    }
  }

  @Test
  void deleteCarById_callsPortalAndReturnsOk() {
    UUID id = UUID.randomUUID();

    ResponseEntity<CarResponse> response = carResource.deleteCarById(id);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    assertNull(response.getBody());

    verify(carPortal).deleteById(id);
  }

  @Test
  void updateCar_mapsRequestToUpdatesAndReturnsMappedResponse() {
    UUID id = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    String color = "Green";

    CarUpdateRequest request = CarUpdateRequest.builder().ownerId(ownerId).color(color).build();

    UserModel owner = UserModel.builder().id(ownerId).firstName("Jane").lastName("Doe").build();

    CarModel updatedModel =
        CarModel.builder()
            .id(id)
            .owner(owner)
            .make("Ford")
            .model("Focus")
            .manufactureYear(Year.of(2018))
            .color(color)
            .build();

    when(carPortal.update(eq(id), any(CarUpdates.class))).thenReturn(updatedModel);

    ResponseEntity<CarResponse> response = carResource.updateCar(id, request);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    CarResponse body = response.getBody();
    assertNotNull(body);

    // verify CarUpdates mapping
    ArgumentCaptor<CarUpdates> updatesCaptor = ArgumentCaptor.forClass(CarUpdates.class);
    verify(carPortal).update(eq(id), updatesCaptor.capture());
    CarUpdates passed = updatesCaptor.getValue();
    assertEquals(ownerId, passed.getOwnerId());
    assertEquals(color, passed.getColor());

    // verify response mapping via CarMapper
    assertEquals(id, body.getId());
    assertEquals(updatedModel.getCarInfo(), body.getCarInfo());
    assertEquals(updatedModel.getUserInfo(), body.getOwnerInfo());
  }

  @Test
  void createCar_mapsRequestToModelAndReturnsCreatedResponse() throws Exception {
    // We don't know exact fields of CarCreationRequest, so we rely on CarMapper + portal behavior
    CarCreationRequest request = CarCreationRequest.builder().build();

    CarModel modelToCreate =
        CarModel.builder()
            .id(UUID.randomUUID())
            .make("Tesla")
            .model("Model 3")
            .manufactureYear(Year.of(2022))
            .color("White")
            .build();

    CarModel createdModel =
        CarModel.builder()
            .id(modelToCreate.getId())
            .make(modelToCreate.getMake())
            .model(modelToCreate.getModel())
            .manufactureYear(modelToCreate.getManufactureYear())
            .color(modelToCreate.getColor())
            .build();

    // Here we *do* mock CarMapper statically because createCar uses CarMapper.toModel + toResponse
    try (MockedStatic<CarMapper> mapperMock = Mockito.mockStatic(CarMapper.class)) {
      mapperMock.when(() -> CarMapper.toModel(request)).thenReturn(modelToCreate);
      when(carPortal.createCar(modelToCreate)).thenReturn(createdModel);

      CarResponse mappedResponse =
          CarResponse.builder()
              .id(createdModel.getId())
              .carInfo(createdModel.getCarInfo())
              .ownerInfo(createdModel.getUserInfo())
              .build();

      mapperMock.when(() -> CarMapper.toResponse(createdModel)).thenReturn(mappedResponse);

      ResponseEntity<CarResponse> response = carResource.createCar(request);

      assertEquals(HttpStatus.CREATED, response.getStatusCode());
      CarResponse body = response.getBody();
      assertNotNull(body);
      assertSame(mappedResponse, body);

      mapperMock.verify(() -> CarMapper.toModel(request));
      verify(carPortal).createCar(modelToCreate);
      mapperMock.verify(() -> CarMapper.toResponse(createdModel));
    }
  }
}
