package com.interview.converter;

import com.interview.domain.QVehicle;
import com.interview.domain.Vehicle;
import com.interview.dto.VehicleRequest;
import com.interview.dto.VehicleResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Optional;

public class VehicleConverter {

    private VehicleConverter() {
        throw new IllegalStateException("Utility class");
    }

    public static Vehicle toVehicle(VehicleRequest vehicleRequest) {
        Vehicle vehicle = new Vehicle();

        vehicle.setBrand(vehicleRequest.getBrand());
        vehicle.setModel(vehicleRequest.getModel());
        vehicle.setMadeYear(vehicleRequest.getMadeYear());
        vehicle.setColor(vehicleRequest.getColor());
        vehicle.setOwnerId(vehicleRequest.getOwnerId());

        return vehicle;
    }

    public static VehicleResponse toVehicleResponse(Vehicle vehicle) {
        VehicleResponse vehicleResponse = new VehicleResponse();

        vehicleResponse.setId(vehicle.getId());
        vehicleResponse.setCreatedAt(vehicle.getCreatedAt());
        vehicleResponse.setUpdatedAt(vehicle.getUpdatedAt());
        vehicleResponse.setBrand(vehicle.getBrand());
        vehicleResponse.setModel(vehicle.getModel());
        vehicleResponse.setMadeYear(vehicle.getMadeYear());
        vehicleResponse.setColor(vehicle.getColor());
        vehicleResponse.setOwnerId(vehicle.getOwnerId());

        return vehicleResponse;
    }

    public static Vehicle apply(Vehicle vehicle, VehicleRequest changes) {
        vehicle.setBrand(changes.getBrand());
        vehicle.setModel(changes.getModel());
        vehicle.setMadeYear(changes.getMadeYear());
        vehicle.setColor(changes.getColor());
        vehicle.setOwnerId(changes.getOwnerId());

        return vehicle;
    }

    /**
     * Convert the requestParams Map to a querydsl {@code Predicate}.
     * If the requestParams Map is empty, and Optional.empty() is returned.
     * <p>
     * For any of the {@code Vehicle} searchable fields (brand, model, madeYear, color),
     * present in the request params, a condition is added to the predicate.
     *
     * @param requestParams the request params Map
     * @return an {@code Optional} of a querydsl {@code Predicate}
     */
    public static Optional<Predicate> toPredicate(Map<String, String> requestParams) {
        if (CollectionUtils.isEmpty(requestParams)) {
            return Optional.empty();
        }

        BooleanBuilder builder = new BooleanBuilder();
        QVehicle vehicle = QVehicle.vehicle;

        // filter by brand
        String brandParam = requestParams.get(Vehicle.BRAND_FIELD);
        if (StringUtils.hasLength(brandParam)) {
            builder.and(vehicle.brand.containsIgnoreCase(brandParam));
        }

        // filter by model
        String modelParam = requestParams.get(Vehicle.MODEL_FIELD);
        if (StringUtils.hasLength(modelParam)) {
            builder.and(vehicle.model.containsIgnoreCase(modelParam));
        }

        // filter by year
        String madeYearParam = requestParams.get(Vehicle.MADE_YEAR_FIELD);
        if (StringUtils.hasLength(madeYearParam)) {
            builder.and(vehicle.madeYear.eq(Integer.parseInt(madeYearParam)));
        }

        // filter by color
        String colorParam = requestParams.get(Vehicle.COLOR_FIELD);
        if (StringUtils.hasLength(colorParam)) {
            builder.and(vehicle.color.containsIgnoreCase(colorParam));
        }

        return Optional.ofNullable(builder.getValue());
    }
}
