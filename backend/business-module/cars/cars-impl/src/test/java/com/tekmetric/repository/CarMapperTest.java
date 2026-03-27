package com.tekmetric.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.tekmetric.UserModel;
import com.tekmetric.UserPortal;
import com.tekmetric.ValidationException;
import com.tekmetric.entity.Car;
import com.tekmetric.exceptions.UserNotFoundException;
import com.tekmetric.model.CarModel;
import java.time.Year;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CarMapperTest {

  @Mock private UserPortal userPortal;

  @InjectMocks private CarMapper carMapper;

  @Test
  void toModels_resolvesOwnersByIds() {
    UUID ownerId1 = UUID.randomUUID();
    UUID ownerId2 = UUID.randomUUID();

    Car car1 =
        Car.builder().id(UUID.randomUUID()).ownerId(ownerId1).make("Toyota").model("RAV4").build();
    Car car2 =
        Car.builder().id(UUID.randomUUID()).ownerId(ownerId2).make("Honda").model("CR-V").build();

    UserModel user1 = UserModel.builder().id(ownerId1).firstName("John").lastName("Doe").build();
    UserModel user2 = UserModel.builder().id(ownerId2).firstName("Jane").lastName("Smith").build();

    when(userPortal.getUsersById(List.of(ownerId1, ownerId2))).thenReturn(List.of(user1, user2));

    List<CarModel> models = carMapper.toModels(List.of(car1, car2));

    assertEquals(2, models.size());
    assertEquals("John", models.get(0).getOwner().getFirstName());
    assertEquals("Jane", models.get(1).getOwner().getFirstName());
  }

  @Test
  void toModel_ownerExists_resolvesSingleOwner() {
    UUID ownerId = UUID.randomUUID();
    Car entity =
        Car.builder()
            .id(UUID.randomUUID())
            .ownerId(ownerId)
            .make("Toyota")
            .model("RAV4")
            .manufactureYear(Year.of(2020))
            .color("red")
            .build();

    UserModel user = UserModel.builder().id(ownerId).firstName("John").lastName("Doe").build();

    when(userPortal.getUserById(ownerId)).thenReturn(user);

    CarModel model = carMapper.toModel(entity);

    assertEquals(ownerId, model.getOwner().getId());
    assertEquals("Toyota", model.getMake());
    assertEquals("RAV4", model.getModel());
  }

  @Test
  void toModel_ownerNotFound_wrapsInValidationException() {
    UUID ownerId = UUID.randomUUID();
    Car entity = Car.builder().id(UUID.randomUUID()).ownerId(ownerId).build();

    when(userPortal.getUserById(ownerId)).thenThrow(new UserNotFoundException("not found"));

    ValidationException ex =
        assertThrows(ValidationException.class, () -> carMapper.toModel(entity));
    assertTrue(ex.getMessage().contains("Unable to fetch owner"));
    assertTrue(ex.getCause() instanceof UserNotFoundException);
  }

  @Test
  void toEntity_mapsModelToEntity() {
    UUID carId = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();

    UserModel owner = UserModel.builder().id(ownerId).firstName("John").lastName("Doe").build();

    CarModel model =
        CarModel.builder()
            .id(carId)
            .owner(owner)
            .make("Toyota")
            .model("RAV4")
            .color("red")
            .manufactureYear(Year.of(2021))
            .build();

    Car entity = carMapper.toEntity(model);

    assertEquals(carId, entity.getId());
    assertEquals(ownerId, entity.getOwnerId());
    assertEquals("Toyota", entity.getMake());
    assertEquals("RAV4", entity.getModel());
    assertEquals("red", entity.getColor());
  }
}
