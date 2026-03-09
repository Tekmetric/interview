package com.interview.repository.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.interview.domain.Vehicle;
import com.interview.domain.Vin;
import com.interview.repository.entity.VehicleEntity;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class VehicleEntityMapperTest {

    private final VehicleEntityMapper mapper = Mappers.getMapper(VehicleEntityMapper.class);

    @Test
    void toEntity() {
        final UUID id = UUID.randomUUID();
        final Vin vin = new Vin("1HGBH41JXMN109186");
        final Vehicle vehicle = new Vehicle(id, vin);

        final VehicleEntity result = mapper.toEntity(vehicle);

        final VehicleEntity expected = new VehicleEntity();
        expected.setId(id);
        expected.setVin(vin);
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("id") // generated
                .isEqualTo(expected);
    }

    @Test
    void updateEntity() {
        final UUID oldId = UUID.randomUUID();
        final Vin oldVin = new Vin("2HGBH41JXMN109186");
        final VehicleEntity entity = new VehicleEntity();
        entity.setId(oldId);
        entity.setVin(oldVin);

        final Vin newVin = new Vin("1HGBH41JXMN109186");
        mapper.updateEntity(new Vehicle(oldId, newVin), entity);

        final VehicleEntity expected = new VehicleEntity();
        expected.setId(UUID.randomUUID());
        expected.setVin(newVin);
        assertThat(entity).usingRecursiveComparison().ignoringFields("id").isEqualTo(expected);
        assertThat(entity.getId()).isEqualTo(oldId);
    }

    @Test
    void toDomain() {
        final UUID id = UUID.randomUUID();
        final Vin vin = new Vin("1HGBH41JXMN109186");
        final VehicleEntity entity = new VehicleEntity();
        entity.setId(id);
        entity.setVin(vin);

        final Vehicle result = mapper.toDomain(entity);

        final Vehicle expected = new Vehicle(id, vin);
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }
}
