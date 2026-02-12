package com.interview.mappers;

import com.interview.domain.Vehicle;
import com.interview.dtos.VehiclePatchDTO;
import com.interview.dtos.VehicleResponseDTO;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

public class VehicleMapperTest {

    private final VehicleMapper mapper = Mappers.getMapper(VehicleMapper.class);

    @Test
    void testEntityToDto() {
        Vehicle vehicle = Vehicle.builder()
                .vin("1HGCM82633A004352")
                .make("Honda")
                .model("Civic")
                .manufactureYear(2020)
                .licensePlate("ABC123")
                .ownerName("John Doe")
                .build();

        VehicleResponseDTO dto = mapper.toDto(vehicle);

        assertThat(dto.vin()).isEqualTo(vehicle.getVin());
        assertThat(dto.make()).isEqualTo(vehicle.getMake());
        assertThat(dto.model()).isEqualTo(vehicle.getModel());
        assertThat(dto.manufactureYear()).isEqualTo(vehicle.getManufactureYear());
        assertThat(dto.licensePlate()).isEqualTo(vehicle.getLicensePlate());
        assertThat(dto.ownerName()).isEqualTo(vehicle.getOwnerName());
    }

    @Test
    void testDtoToEntity() {
        VehicleResponseDTO dto = VehicleResponseDTO.builder()
                .vin("2C3KA43R08H315832")
                .make("Chrysler")
                .model("300")
                .manufactureYear(2022)
                .licensePlate("XYZ789")
                .ownerName("Jane Smith")
                .build();

        Vehicle entity = mapper.toEntity(dto);

        assertThat(entity.getVin()).isEqualTo(dto.vin());
        assertThat(entity.getMake()).isEqualTo(dto.make());
        assertThat(entity.getModel()).isEqualTo(dto.model());
        assertThat(entity.getManufactureYear()).isEqualTo(dto.manufactureYear());
        assertThat(entity.getLicensePlate()).isEqualTo(dto.licensePlate());
        assertThat(entity.getOwnerName()).isEqualTo(dto.ownerName());
    }

    @Test
    void testUpdateEntityFromDto() {
        Vehicle entity = Vehicle.builder()
                .vin("OLDVIN12345678901")
                .make("Ford")
                .model("Focus")
                .manufactureYear(2018)
                .licensePlate("OLD123")
                .ownerName("Old Owner")
                .build();

        VehiclePatchDTO dto = VehiclePatchDTO.builder()
                .vin("NEWVIN12345678901")
                .make("Tesla")
                .model("Model 3")
                .manufactureYear(2023)
                .licensePlate("NEW123")
                .ownerName("New Owner")
                .build();

        mapper.patchEntity(dto, entity);

        assertThat(entity.getVin()).isEqualTo(dto.vin());
        assertThat(entity.getMake()).isEqualTo(dto.make());
        assertThat(entity.getModel()).isEqualTo(dto.model());
        assertThat(entity.getManufactureYear()).isEqualTo(dto.manufactureYear());
        assertThat(entity.getLicensePlate()).isEqualTo(dto.licensePlate());
        assertThat(entity.getOwnerName()).isEqualTo(dto.ownerName());
    }

    @Test
    void testUpdateEntityFromDto_NullDoesNotOverwrite() {
        Vehicle entity = Vehicle.builder().vin("VIN12345678901234").make("Ford").build();

        VehiclePatchDTO dto = VehiclePatchDTO.builder().vin(null).make("Toyota").build();

        mapper.patchEntity(dto, entity);

        assertThat(entity.getVin()).isEqualTo("VIN12345678901234");
        assertThat(entity.getMake()).isEqualTo("Toyota");
    }
}
