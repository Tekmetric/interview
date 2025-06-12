package com.interview.repository;

import com.interview.entity.Owner;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class OwnerSpecifications {

  public static Specification<Owner> fuzzySearch(final String query) {
    return (root, cq, builder) -> {
      if (query == null || query.trim().isEmpty()) {
        return builder.conjunction(); // always true, returns all
      }

      final String likeQuery = "%" + query.toLowerCase() + "%";
      // Join with cars (child entity)
      final var carJoin = root.join("cars", JoinType.LEFT);

      return builder.or(
          builder.like(builder.lower(root.get("name")), likeQuery),
          builder.like(builder.lower(root.get("address")), likeQuery),
          builder.like(builder.lower(carJoin.get("vin")), likeQuery),
          builder.like(builder.lower(carJoin.get("model")), likeQuery));
    };
  }
}
