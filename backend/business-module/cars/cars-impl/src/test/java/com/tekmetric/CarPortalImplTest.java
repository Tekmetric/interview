package com.tekmetric;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.tekmetric.entity.Car;
import com.tekmetric.model.CarFilter;
import com.tekmetric.model.CarModel;
import com.tekmetric.repository.CarDAO;
import com.tekmetric.repository.CarMapper;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class CarPortalImplTest {

  @Mock private CarDAO carDAO;
  @Mock private CarMapper carMapper;

  @InjectMocks private CarPortalImpl carPortal;

  @Test
  void getById_carExists_returnsModel() {
    UUID id = UUID.randomUUID();
    Car entity = Car.builder().id(id).make("Toyota").model("RAV4").build();
    CarModel model = CarModel.builder().id(id).make("Toyota").model("RAV4").build();

    when(carDAO.findById(id)).thenReturn(Optional.of(entity));
    when(carMapper.toModel(entity)).thenReturn(model);

    CarModel result = carPortal.getById(id);

    assertEquals(id, result.getId());
  }

  @Test
  void getById_carMissing_throwsCarNotFoundException() {
    UUID id = UUID.randomUUID();
    when(carDAO.findById(id)).thenReturn(Optional.empty());

    assertThrows(CarNotFoundException.class, () -> carPortal.getById(id));
  }

  @Test
  void getCars_buildsSpecificationAndMapsList() {
    CarFilter filter = CarFilter.builder().make("Toyota").model("RAV4").build();
    Pageable pageable = PageRequest.of(0, 10);

    Car entity = Car.builder().id(UUID.randomUUID()).make("Toyota").model("RAV4").build();
    Page<Car> page = new PageImpl<>(List.of(entity), pageable, 1);

    CarModel model = CarModel.builder().id(entity.getId()).make("Toyota").model("RAV4").build();

    when(carDAO.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
    when(carMapper.toModels(List.of(entity))).thenReturn(List.of(model));

    Page<CarModel> result = carPortal.getCars(filter, pageable);

    assertEquals(1, result.getTotalElements());
    assertEquals("Toyota", result.getContent().get(0).getMake());
  }

  @Test
  void createCar_validMakeAndModel_savesEntityAndReturnsModel() {
    CarModel request =
        CarModel.builder()
            .make("Toyota")
            .model("RAV4")
            .color("red")
            .manufactureYear(Year.of(2020))
            .build();

    Car savedEntity =
        Car.builder()
            .id(UUID.randomUUID())
            .make("Toyota")
            .model("RAV4")
            .color("red")
            .manufactureYear(Year.of(2020))
            .build();

    CarModel mapped =
        CarModel.builder()
            .id(savedEntity.getId())
            .make("Toyota")
            .model("RAV4")
            .color("red")
            .manufactureYear(Year.of(2020))
            .build();

    when(carDAO.save(any(Car.class))).thenReturn(savedEntity);
    when(carMapper.toModel(savedEntity)).thenReturn(mapped);

    CarModel result = carPortal.createCar(request);

    assertEquals("Toyota", result.getMake());
    assertEquals("RAV4", result.getModel());
  }

  @Test
  void createCar_invalidModelForMake_throwsValidationException() {
    CarModel request =
        CarModel.builder()
            .make("Toyota")
            .model("Civic") // invalid for Toyota
            .color("red")
            .manufactureYear(Year.of(2020))
            .build();

    assertThrows(ValidationException.class, () -> carPortal.createCar(request));
  }

  @Test
  void deleteById_carWithoutOwner_deletesCar() {
    UUID id = UUID.randomUUID();
    Car entity = Car.builder().id(id).build();
    CarModel model = CarModel.builder().id(id).owner(null).build();

    when(carDAO.findById(id)).thenReturn(Optional.of(entity));
    when(carMapper.toModel(entity)).thenReturn(model);

    carPortal.deleteById(id);

    verify(carDAO).deleteById(id);
  }

  @Test
  void deleteById_carWithOwner_throwsValidationException() {
    UUID id = UUID.randomUUID();
    Car entity = Car.builder().id(id).build();

    UserModel owner =
        UserModel.builder().id(UUID.randomUUID()).firstName("John").lastName("Doe").build();
    CarModel model = CarModel.builder().id(id).owner(owner).build();

    when(carDAO.findById(id)).thenReturn(Optional.of(entity));
    when(carMapper.toModel(entity)).thenReturn(model);

    assertThrows(ValidationException.class, () -> carPortal.deleteById(id));
    verify(carDAO, never()).deleteById(any());
  }
}
