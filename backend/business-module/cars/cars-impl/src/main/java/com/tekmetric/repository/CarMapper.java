package com.tekmetric.repository;

import com.tekmetric.UserModel;
import com.tekmetric.UserPortal;
import com.tekmetric.ValidationException;
import com.tekmetric.entity.Car;
import com.tekmetric.exceptions.UserNotFoundException;
import com.tekmetric.model.CarModel;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CarMapper {
  private final UserPortal userPortal;

  public List<CarModel> toModels(List<Car> entities) {
    List<UUID> userIdsToFetch =
        entities.stream().map(Car::getOwnerId).filter(Objects::nonNull).distinct().toList();

    List<UserModel> fetchedUsers = userPortal.getUsersById(userIdsToFetch);
    Map<UUID, UserModel> usersIdsMap =
        fetchedUsers.stream().collect(Collectors.toMap(UserModel::getId, Function.identity()));
    return entities.stream()
        .map(
            entity ->
                CarModel.builder()
                    .id(entity.getId())
                    .owner(usersIdsMap.get(entity.getOwnerId()))
                    .color(entity.getColor())
                    .make(entity.getMake())
                    .model(entity.getModel())
                    .manufactureYear(entity.getManufactureYear())
                    .build())
        .toList();
  }

  public CarModel toModel(Car entity) {
    UserModel owner = null;
    if (entity.getOwnerId() != null) {
      try {
        owner = userPortal.getUserById(entity.getOwnerId());
      } catch (UserNotFoundException ex) {
        throw new ValidationException(
            "Unable to fetch owner (id=%s) details information".formatted(entity.getOwnerId()), ex);
      }
    }

    return CarModel.builder()
        .id(entity.getId())
        .owner(owner)
        .color(entity.getColor())
        .make(entity.getMake())
        .model(entity.getModel())
        .manufactureYear(entity.getManufactureYear())
        .build();
  }

  public Car toEntity(CarModel model) {
    return Car.builder()
        .id(model.getId())
        .ownerId(model.getOwner() == null ? null : model.getOwner().getId())
        .model(model.getModel())
        .make(model.getMake())
        .color(model.getColor())
        .build();
  }
}
