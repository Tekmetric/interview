package com.tekmetric;

import static com.tekmetric.repository.CarQuery.*;
import static com.tekmetric.util.PageUtil.mapList;

import com.tekmetric.entity.Car;
import com.tekmetric.entity.CarMake;
import com.tekmetric.exceptions.UserNotFoundException;
import com.tekmetric.model.CarFilter;
import com.tekmetric.model.CarModel;
import com.tekmetric.model.CarUpdates;
import com.tekmetric.repository.CarDAO;
import com.tekmetric.repository.CarMapper;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CarPortalImpl implements CarPortal {

  private final CarDAO carDAO;
  private final CarMapper carMapper;
  private final UserPortal userPortal;

  @Override
  public CarModel getById(UUID id) {
    Optional<Car> car = carDAO.findById(id);
    if (car.isEmpty()) {
      throw new CarNotFoundException("There is no car with id: " + id);
    }
    return carMapper.toModel(car.get());
  }

  @Override
  public Page<CarModel> getCars(CarFilter filter, Pageable pageable) {
    Specification<Car> spec =
        hasMake(filter.getMake())
            .and(hasModel(filter.getModel()))
            .and(hasYear(filter.getYear()))
            .and(hasColor(filter.getColor()))
            .and(hasOwner(filter.getOwnerFilter()));
    Page<Car> cars = carDAO.findAll(spec, pageable);
    return mapList(cars, carMapper::toModels);
  }

  @Override
  public CarModel createCar(CarModel car) {
    CarMake make = CarMake.fromDisplayName(car.getMake());
    if (!make.supportsModel(car.getModel())) {
      throw new ValidationException(
          "Model '%s' is not valid for make '%s'. Allowed: %s"
              .formatted(car.getModel(), make.getDisplayName(), make.getModelNames()));
    }
    Car entity =
        Car.builder()
            .make(make.getDisplayName())
            .model(car.getModel())
            .manufactureYear(car.getManufactureYear())
            .color(car.getColor())
            .build();

    return carMapper.toModel(carDAO.save(entity));
  }

  @Override
  public CarModel update(UUID id, CarUpdates updates) {
    UUID ownerIdToSet = null;
    if (updates.getOwnerId() != null) {
      try {
        UserModel userById = userPortal.getUserById(updates.getOwnerId());
        ownerIdToSet = userById.getId();
      } catch (UserNotFoundException ex) {
        throw new ValidationException(
            "Unable to fetch new owner (id=%s) details information".formatted(updates.getOwnerId()),
            ex);
      }
    }

    int updatedRows = carDAO.updateOwnerAndColor(id, ownerIdToSet, updates.getColor());
    if (updatedRows == 0) {
      throw new CarNotFoundException("There is no car with id: " + id);
    }

    Car updated =
        carDAO
            .findById(id)
            .orElseThrow(() -> new CarNotFoundException("There is no car with id: " + id));

    return carMapper.toModel(updated);
  }

  @Override
  public void deleteById(UUID id) {
    CarModel car = getById(id);
    if (car.getOwner() != null) {
      throw new ValidationException(
          "Car was sold to " + car.getUserInfo() + ". Could not be removed.");
    }
    carDAO.deleteById(id);
  }
}
