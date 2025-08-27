package com.interview.repository;

import com.interview.domain.Vehicle;
import com.interview.filter.VehicleQueryFilter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static com.interview.domain.Vehicle.VehicleSearchableFields.ID;
import static com.interview.domain.Vehicle.VehicleSearchableFields.PRODUCTION_YEAR;
import static com.interview.domain.Vehicle.VehicleSearchableFields.TYPE;
import static com.interview.domain.Vehicle.VehicleSearchableFields.VIN;

@Component
public class VehicleSpecificationBuilder {
    
    public Specification<Vehicle> buildSpecification(final VehicleQueryFilter filter) {
        return ((root, query, criteriaBuilder) -> {
            final List<Predicate> predicates = buildPredicates(filter, root, criteriaBuilder);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }

    private List<Predicate> buildPredicates(VehicleQueryFilter filter, Root<Vehicle> root, CriteriaBuilder criteriaBuilder) {
        final List<Predicate> predicates = new ArrayList<>();

        if (filter == null || filter.isEmpty()) {
            return predicates;
        }

        if (!CollectionUtils.isEmpty(filter.includingIds())) {
            predicates.add(root.get(ID.getFieldName()).in(filter.includingIds()));
        }

        if (!CollectionUtils.isEmpty(filter.excludingIds())) {
            predicates.add(criteriaBuilder.not(root.get(ID.getFieldName()).in(filter.excludingIds())));
        }

        if (filter.productionYearFrom() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get(PRODUCTION_YEAR.getFieldName()), filter.productionYearFrom().getValue()));
        }

        if (filter.productionYearTo() != null) {
            predicates.add(criteriaBuilder.lessThan(
                    root.get(PRODUCTION_YEAR.getFieldName()), filter.productionYearTo().getValue()));
        }

        if (!CollectionUtils.isEmpty(filter.includingVins())) {
            predicates.add(root.get(VIN.getFieldName()).in(filter.includingVins()));
        }

        if (!CollectionUtils.isEmpty(filter.excludingVins())) {
            predicates.add(criteriaBuilder.not(root.get(VIN.getFieldName()).in(filter.excludingVins())));
        }
        if (!CollectionUtils.isEmpty(filter.includingVehicleTypes())) {
            predicates.add(root.get(TYPE.getFieldName()).in(filter.includingVehicleTypes()));
        }

        if (!CollectionUtils.isEmpty(filter.excludingVehicleTypes())) {
            predicates.add(criteriaBuilder.not(root.get(TYPE.getFieldName()).in(filter.excludingVehicleTypes())));
        }
        return predicates;
    }

}
