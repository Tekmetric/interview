package com.interview.jpa.specification;

import com.interview.jpa.entity.Flight;
import com.interview.jpa.entity.enums.FlightEnum;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class FlightSpecifications {
    private FlightSpecifications() {}

    public static Specification<Flight> hasDepartureAirport(String iata) {
        return (root, q, cb) -> (iata == null || iata.isBlank())
                ? cb.conjunction()
                : cb.equal(cb.upper(root.get("departureAirport")), iata.trim().toUpperCase());
    }

    public static Specification<Flight> hasArrivalAirport(String iata) {
        return (root, q, cb) -> (iata == null || iata.isBlank())
                ? cb.conjunction()
                : cb.equal(cb.upper(root.get("arrivalAirport")), iata.trim().toUpperCase());
    }

    public static Specification<Flight> hasStatus(FlightEnum.Status status) {
        return (root, q, cb) -> (status == null)
                ? cb.conjunction()
                : cb.equal(root.get("status"), status);
    }

    public static Specification<Flight> hasPlaneId(Integer planeId) {
        return (root, q, cb) -> (planeId == null)
                ? cb.conjunction()
                : cb.equal(root.get("plane").get("id"), planeId);
    }

    /** depFrom/depTo are inclusive; if only one is provided we use gte/lte respectively. */
    public static Specification<Flight> departureBetween(LocalDateTime depFrom, LocalDateTime depTo) {
        return (root, q, cb) -> {
            if (depFrom == null && depTo == null) return cb.conjunction();
            if (depFrom != null && depTo != null) {
                return cb.between(root.get("departureTime"), depFrom, depTo);
            }
            if (depFrom != null) {
                return cb.greaterThanOrEqualTo(root.get("departureTime"), depFrom);
            }
            return cb.lessThanOrEqualTo(root.get("departureTime"), depTo);
        };
    }
}
