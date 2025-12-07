package com.tekmetric;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.tekmetric.entity.Car;
import com.tekmetric.exceptions.UserNotFoundException;
import com.tekmetric.model.CarModel;
import com.tekmetric.model.CarUpdates;
import com.tekmetric.repository.CarDAO;
import com.tekmetric.repository.CarMapper;
import java.time.Year;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CarPortalImplUpdateTest {

  @Mock private CarDAO carDAO;
  @Mock private CarMapper carMapper;
  @Mock private UserPortal userPortal;

  @InjectMocks private CarPortalImpl carPortal;

  @Test
  void update_whenBothOwnerAndColorProvided_updatesBothInSingleCall() {
    UUID carId = UUID.randomUUID();
    UUID newOwnerId = UUID.randomUUID();
    String newColor = "blue";

    CarUpdates updates = CarUpdates.builder().ownerId(newOwnerId).color(newColor).build();

    UserModel userModel =
        UserModel.builder().id(newOwnerId).firstName("John").lastName("Doe").build();

    Car entityAfterUpdate =
        Car.builder()
            .id(carId)
            .ownerId(newOwnerId)
            .color(newColor)
            .make("Toyota")
            .model("RAV4")
            .manufactureYear(Year.of(2020))
            .build();

    CarModel expectedModel =
        com.tekmetric.model.CarModel.builder()
            .id(carId)
            .owner(userModel)
            .color(newColor)
            .make("Toyota")
            .model("RAV4")
            .manufactureYear(Year.of(2020))
            .build();

    when(userPortal.getUserById(newOwnerId)).thenReturn(userModel);
    when(carDAO.updateOwnerAndColor(carId, newOwnerId, newColor)).thenReturn(1);
    when(carDAO.findById(carId)).thenReturn(Optional.of(entityAfterUpdate));
    when(carMapper.toModel(entityAfterUpdate)).thenReturn(expectedModel);

    CarModel result = carPortal.update(carId, updates);

    assertEquals(expectedModel, result);

    verify(userPortal).getUserById(newOwnerId);
    verify(carDAO).updateOwnerAndColor(carId, newOwnerId, newColor);
    verify(carDAO).findById(carId);
    verify(carMapper).toModel(entityAfterUpdate);
  }

  @Test
  void update_whenOnlyColorProvided_updatesColorAndLeavesOwnerUntouched() {
    UUID carId = UUID.randomUUID();
    String newColor = "red";

    CarUpdates updates = CarUpdates.builder().color(newColor).build();

    Car entityAfterUpdate =
        Car.builder()
            .id(carId)
            .ownerId(UUID.randomUUID())
            .color(newColor)
            .make("BMW")
            .model("X5")
            .manufactureYear(Year.of(2019))
            .build();

    CarModel expectedModel =
        com.tekmetric.model.CarModel.builder()
            .id(carId)
            .color(newColor)
            .make("BMW")
            .model("X5")
            .manufactureYear(Year.of(2019))
            .build();

    when(carDAO.updateOwnerAndColor(carId, null, newColor)).thenReturn(1);
    when(carDAO.findById(carId)).thenReturn(Optional.of(entityAfterUpdate));
    when(carMapper.toModel(entityAfterUpdate)).thenReturn(expectedModel);

    CarModel result = carPortal.update(carId, updates);

    assertEquals(expectedModel, result);

    verify(userPortal, never()).getUserById(any());
    verify(carDAO).updateOwnerAndColor(carId, null, newColor);
    verify(carDAO).findById(carId);
    verify(carMapper).toModel(entityAfterUpdate);
  }

  @Test
  void update_whenOnlyOwnerProvided_updatesOwnerAndLeavesColorUntouched() {
    UUID carId = UUID.randomUUID();
    UUID newOwnerId = UUID.randomUUID();

    CarUpdates updates = CarUpdates.builder().ownerId(newOwnerId).build();

    UserModel userModel =
        UserModel.builder().id(newOwnerId).firstName("Jane").lastName("Smith").build();

    Car entityAfterUpdate =
        Car.builder()
            .id(carId)
            .ownerId(newOwnerId)
            .color("black")
            .make("Tesla")
            .model("3")
            .manufactureYear(Year.of(2021))
            .build();

    CarModel expectedModel =
        com.tekmetric.model.CarModel.builder()
            .id(carId)
            .owner(userModel)
            .color("black")
            .make("Tesla")
            .model("3")
            .manufactureYear(Year.of(2021))
            .build();

    when(userPortal.getUserById(newOwnerId)).thenReturn(userModel);
    when(carDAO.updateOwnerAndColor(carId, newOwnerId, null)).thenReturn(1);
    when(carDAO.findById(carId)).thenReturn(Optional.of(entityAfterUpdate));
    when(carMapper.toModel(entityAfterUpdate)).thenReturn(expectedModel);

    CarModel result = carPortal.update(carId, updates);

    assertEquals(expectedModel, result);

    verify(userPortal).getUserById(newOwnerId);
    verify(carDAO).updateOwnerAndColor(carId, newOwnerId, null);
    verify(carDAO).findById(carId);
    verify(carMapper).toModel(entityAfterUpdate);
  }

  @Test
  void update_whenNewOwnerNotFound_throwsValidationExceptionAndDoesNotUpdate() {
    UUID carId = UUID.randomUUID();
    UUID newOwnerId = UUID.randomUUID();

    CarUpdates updates = CarUpdates.builder().ownerId(newOwnerId).build();

    when(userPortal.getUserById(newOwnerId)).thenThrow(new UserNotFoundException("User not found"));

    ValidationException ex =
        assertThrows(ValidationException.class, () -> carPortal.update(carId, updates));

    assertTrue(ex.getMessage().contains("Unable to fetch new owner (id=%s)".formatted(newOwnerId)));

    verify(carDAO, never()).updateOwnerAndColor(any(), any(), any());
    verify(carDAO, never()).findById(any());
    verify(carMapper, never()).toModel(any());
  }

  @Test
  void update_whenNoRowsUpdated_throwsCarNotFound() {
    UUID carId = UUID.randomUUID();
    String newColor = "green";

    CarUpdates updates = CarUpdates.builder().color(newColor).build();

    when(carDAO.updateOwnerAndColor(carId, null, newColor)).thenReturn(0);

    assertThrows(CarNotFoundException.class, () -> carPortal.update(carId, updates));

    verify(carDAO).updateOwnerAndColor(carId, null, newColor);
    verify(carDAO, never()).findById(any());
    verify(carMapper, never()).toModel(any());
  }
}
