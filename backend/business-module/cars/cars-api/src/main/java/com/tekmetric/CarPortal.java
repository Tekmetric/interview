package com.tekmetric;

import com.tekmetric.model.CarFilter;
import com.tekmetric.model.CarModel;
import com.tekmetric.model.CarUpdates;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CarPortal {
  CarModel getById(UUID id);

  CarModel update(UUID id, CarUpdates updates);

  void deleteById(UUID id);

  Page<CarModel> getCars(CarFilter filter, Pageable pageable);

  CarModel createCar(CarModel model);
}
