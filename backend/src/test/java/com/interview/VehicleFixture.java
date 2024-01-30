package com.interview;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.interview.model.Vehicle;
import com.interview.resource.dto.VehicleDto;

public class VehicleFixture {

    public static final String LICENSE_PLATE = "TE-ST-1234";

    public static Page<Vehicle> withPage() {
        return new PageImpl<>(
            Stream.of(
                withLicensePlate("DO-BR-512"),
                withLicensePlate("BE-TK-6231"),
                withLicensePlate("R-UYA-45")
            )
            .collect(Collectors.toList())
        );
    }

    public static Page<VehicleDto> dtoWithPage() {
        return new PageImpl<>(
            Stream.of(
                dtoWithLicensePlate("DO-BR-512"),
                dtoWithLicensePlate("BE-TK-6231"),
                dtoWithLicensePlate("R-UYA-45")
            )
            .collect(Collectors.toList())
        );
    }

    public static Vehicle withId(Long id) {
        return Vehicle.builder().id(id).licensePlate(LICENSE_PLATE).build();
    }

    public static VehicleDto dtoWithId(Long id) {
        return VehicleDto.builder().id(id).licensePlate(LICENSE_PLATE).build();
    }

    public static Vehicle withLicensePlate(String licensePlate) {
        return Vehicle.builder().licensePlate(licensePlate).build();
    }

    public static VehicleDto dtoWithLicensePlate(String licensePlate) {
        return VehicleDto.builder().licensePlate(licensePlate).build();
    }
}
