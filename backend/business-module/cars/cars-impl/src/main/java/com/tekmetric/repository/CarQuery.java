package com.tekmetric.repository;

import com.tekmetric.entity.Car;
import com.tekmetric.model.OwnerFilter;
import java.time.Year;
import org.springframework.data.jpa.domain.Specification;

public class CarQuery {
  public static Specification<Car> hasMake(String make) {
    return (root, query, cb) -> make == null ? null : cb.equal(root.get("make"), make);
  }

  public static Specification<Car> hasModel(String model) {
    return (root, query, cb) -> model == null ? null : cb.equal(root.get("model"), model);
  }

  public static Specification<Car> hasYear(Integer year) {
    return (root, query, cb) ->
        year == null ? null : cb.equal(root.get("manufactureYear"), Year.of(year));
  }

  public static Specification<Car> hasColor(String color) {
    return (root, query, cb) -> color == null ? null : cb.equal(root.get("color"), color);
  }

  public static Specification<Car> hasOwner(OwnerFilter state) {
    return (root, query, cb) -> {
      if (state == null) {
        return cb.conjunction(); // no filter – return everything
      }

      return switch (state) {
        case NO_OWNER -> cb.isNull(root.get("ownerId")); // if using relation field
        case WITH_OWNER -> cb.isNotNull(root.get("ownerId"));
        default -> cb.conjunction();
      };
    };
  }
}
