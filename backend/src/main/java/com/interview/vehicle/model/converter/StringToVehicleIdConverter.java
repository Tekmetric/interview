package com.interview.vehicle.model.converter;

import com.interview.vehicle.model.VehicleId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;

public class StringToVehicleIdConverter implements Converter<String, VehicleId> {

    @Override
    public VehicleId convert(String source) {
        Assert.notNull(source, "Vehicle Id cannot be null");
        Assert.isTrue(!source.isBlank(),"Vehicle Id cannot be blank");

        try {
            long id = Long.parseLong(source);
            return VehicleId.fromValue(id);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Vehicle Id must be valid");
        }
    }
}
