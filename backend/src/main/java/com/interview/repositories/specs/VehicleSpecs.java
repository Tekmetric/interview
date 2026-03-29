package com.interview.repositories.specs;

import com.interview.domain.Vehicle;
import com.interview.dtos.VehicleSearchCriteriaDTO;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class VehicleSpecs {

    public static Specification<Vehicle> from(VehicleSearchCriteriaDTO criteria) {
        Specification<Vehicle> spec = Specification.anyOf();
        if (criteria == null) return spec;

        if (hasValue(criteria.vin())) {
            spec = spec.and(hasVin(criteria.vin()));
        }

        if (hasValue(criteria.make())) {
            spec = spec.and(hasMake(criteria.make()));
        }

        if (hasValue(criteria.model())) {
            spec = spec.and(hasModel(criteria.model()));
        }

        if (criteria.hasExactYear()) {
            spec = spec.and(hasManufactureYear(criteria.manufactureYear()));
        } else if (criteria.hasYearRange()) {
            spec = spec.and(manufacturedBetween(criteria.yearFrom(), criteria.yearTo()));
        }

        if (hasValue(criteria.licensePlate())) {
            spec = spec.and(hasLicensePlate(criteria.licensePlate()));
        }

        if (hasValue(criteria.ownerName())) {
            spec = spec.and(hasOwnerContaining(criteria.ownerName()));
        }

        if (criteria.makes() != null && !criteria.makes().isEmpty()) {
            spec = spec.and(hasMakeIn(criteria.makes()));
        }

        if (criteria.models() != null && !criteria.models().isEmpty()) {
            spec = spec.and(hasModelIn(criteria.models()));
        }

        if (criteria.hasLicensePlate() != null) {
            if (criteria.hasLicensePlate()) {
                spec = spec.and(hasLicensePlateAssigned());
            } else {
                spec = spec.and(hasNoLicensePlate());
            }
        }

        if (criteria.isLuxuryVehicle() != null && criteria.isLuxuryVehicle()) {
            spec = spec.and(isLuxuryVehicle());
        }

        return spec;
    }

    public static Specification<Vehicle> hasVin(String vin) {
        return (root, query, builder) ->
                builder.equal(builder.upper(root.get("vin")), vin.toUpperCase());
    }

    public static Specification<Vehicle> hasMake(String make) {
        return (root, query, builder) ->
                builder.equal(builder.lower(root.get("make")), make.toLowerCase());
    }

    public static Specification<Vehicle> hasModel(String model) {
        return (root, query, builder) ->
                builder.equal(builder.lower(root.get("model")), model.toLowerCase());
    }

    public static Specification<Vehicle> hasManufactureYear(Integer year) {
        return (root, query, builder) ->
                builder.equal(root.get("manufactureYear"), year);
    }

    public static Specification<Vehicle> manufacturedBetween(Integer yearFrom, Integer yearTo) {
        return (root, query, builder) -> {
            if (yearFrom == null && yearTo == null) {
                return builder.conjunction();
            }
            if (yearFrom == null) {
                return builder.lessThanOrEqualTo(root.get("manufactureYear"), yearTo);
            }
            if (yearTo == null) {
                return builder.greaterThanOrEqualTo(root.get("manufactureYear"), yearFrom);
            }
            return builder.between(root.get("manufactureYear"), yearFrom, yearTo);
        };
    }

    public static Specification<Vehicle> hasLicensePlate(String licensePlate) {
        return (root, query, builder) ->
                builder.equal(builder.upper(root.get("licensePlate")), licensePlate.toUpperCase());
    }

    public static Specification<Vehicle> hasOwnerContaining(String ownerName) {
        return (root, query, builder) ->
                builder.like(builder.lower(root.get("ownerName")),
                        "%" + ownerName.toLowerCase() + "%");
    }

    public static Specification<Vehicle> hasMakeIn(Set<String> makes) {
        return (root, query, builder) -> {
            Set<String> lowerCaseMakes = makes.stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());
            return builder.lower(root.get("make")).in(lowerCaseMakes);
        };
    }

    public static Specification<Vehicle> hasModelIn(Set<String> models) {
        return (root, query, builder) -> {
            Set<String> lowerCaseModels = models.stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());
            return builder.lower(root.get("model")).in(lowerCaseModels);
        };
    }

    public static Specification<Vehicle> hasLicensePlateAssigned() {
        return (root, query, builder) ->
                builder.and(
                        builder.isNotNull(root.get("licensePlate")),
                        builder.notEqual(root.get("licensePlate"), "")
                );
    }

    public static Specification<Vehicle> hasNoLicensePlate() {
        return (root, query, builder) ->
                builder.or(
                        builder.isNull(root.get("licensePlate")),
                        builder.equal(root.get("licensePlate"), "")
                );
    }

    public static Specification<Vehicle> isLuxuryVehicle() {
        List<String> luxuryMakes = List.of("bmw", "audi", "porsche", "tesla");
        return (root, query, builder) ->
                builder.and(
                        builder.lower(root.get("make")).in(luxuryMakes)
                );
    }

    private static boolean hasValue(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
