package com.interview.service;

import com.interview.dto.VehiclePatchRequest;
import com.interview.dto.VehicleRequest;
import com.interview.dto.VehicleResponse;
import com.interview.entity.Vehicle;

final class VehicleMapper {

    private VehicleMapper() {
    }

    static Vehicle toEntity(VehicleRequest r) {
        return new Vehicle(r.vin().toUpperCase(), r.make(), r.model(), r.year(), r.licensePlate(), r.mileage());
    }

    static void applyPatch(VehiclePatchRequest r, Vehicle v) {
        if (r.vin() != null) v.setVin(r.vin().toUpperCase());
        if (r.make() != null) v.setMake(r.make());
        if (r.model() != null) v.setModel(r.model());
        if (r.year() != null) v.setYear(r.year());
        if (r.licensePlate() != null) v.setLicensePlate(r.licensePlate());
        if (r.mileage() != null) v.setMileage(r.mileage());
    }

    static VehicleResponse toResponse(Vehicle v) {
        return new VehicleResponse(
                v.getId(),
                v.getVin(),
                v.getMake(),
                v.getModel(),
                v.getYear(),
                v.getLicensePlate(),
                v.getMileage(),
                v.getCreatedAt(),
                v.getUpdatedAt());
    }
}
