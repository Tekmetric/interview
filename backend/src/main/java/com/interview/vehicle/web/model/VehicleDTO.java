package com.interview.vehicle.web.model;

import com.interview.vehicle.model.Vehicle;
import com.interview.vehicle.model.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
@AllArgsConstructor
public class VehicleDTO implements Serializable {

    private Long id;

    private VehicleType type;

    private Integer fabricationYear;

    private String make;

    private String model;

    public static VehicleDTO fromEntity(Vehicle entity) {
        return VehicleDTO.builder()
                .id(entity.id().value())
                .type(entity.type())
                .fabricationYear(entity.fabricationYear().getValue())
                .make(entity.make())
                .model(entity.model())
                .build();
    }
}
