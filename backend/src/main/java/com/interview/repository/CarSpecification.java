package com.interview.repository;

import com.interview.model.Car;
import com.interview.model.CarStatus;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

/**
 * JPA Specifications for dynamic Car filtering.
 */
public final class CarSpecification {

    private CarSpecification() {
    }

    public static Specification<Car> hasStatus(CarStatus status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Car> hasBrand(String brand) {
        return (root, query, cb) -> brand == null ? null : cb.equal(root.get("brand"), brand);
    }

    public static Specification<Car> hasBasePriceMin(BigDecimal minPrice) {
        return (root, query, cb) -> minPrice == null ? null : cb.greaterThanOrEqualTo(root.get("basePrice"), minPrice);
    }

    public static Specification<Car> hasBasePriceMax(BigDecimal maxPrice) {
        return (root, query, cb) -> maxPrice == null ? null : cb.lessThanOrEqualTo(root.get("basePrice"), maxPrice);
    }
}
