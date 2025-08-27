package com.interview.filter;

import com.interview.domain.VehicleType;
import lombok.Builder;

import java.time.Year;
import java.util.Set;

@Builder
public record VehicleQueryFilter(Set<Long> includingIds, Set<Long> excludingIds, Set<VehicleType> includingVehicleTypes,
                                 Set<VehicleType> excludingVehicleTypes, Year productionYearFrom,
                                 Year productionYearTo,
                                 Set<String> includingVins, Set<String> excludingVins) {

    public boolean isEmpty() {
        return (includingIds == null || includingIds.isEmpty()) &&
                (excludingIds == null || excludingIds.isEmpty()) &&
                (includingVehicleTypes == null || includingVehicleTypes.isEmpty()) &&
                (excludingVehicleTypes == null || excludingVehicleTypes.isEmpty()) &&
                productionYearFrom == null && productionYearTo == null &&
                (includingVins == null || includingVins.isEmpty()) &&
                (excludingVins == null || excludingVins.isEmpty());
    }

    public static VehicleQueryFilter forId(long id) {
        return VehicleQueryFilter.builder()
                .includingIds(Set.of(id)).build();
    }

    public static VehicleQueryFilter forVin(String vin) {
        return VehicleQueryFilter.builder()
                .includingVins(Set.of(vin)).build();
    }

}
