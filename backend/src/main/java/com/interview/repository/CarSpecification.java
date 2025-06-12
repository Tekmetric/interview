package com.interview.repository;

import com.interview.entity.Car;
import org.springframework.data.jpa.domain.Specification;

public class CarSpecification {

  public static Specification<Car> fuzzySearch(final String query) {
    return (root, cq, builder) -> {
      if (query == null || query.trim().isEmpty()) {
        return builder.conjunction(); // always true, returns all
      }
      final String likePattern = "%" + query.toLowerCase() + "%";
      return builder.or(
          builder.like(builder.lower(root.get("model")), likePattern),
          builder.like(builder.lower(root.get("vin")), likePattern));
    };
  }
}
