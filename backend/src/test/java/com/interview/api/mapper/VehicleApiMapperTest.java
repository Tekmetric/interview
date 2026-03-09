package com.interview.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.interview.api.request.VehicleRequest;
import com.interview.api.response.VehicleResponse;
import com.interview.domain.Vehicle;
import com.interview.domain.Vin;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class VehicleApiMapperTest {

    private final VehicleApiMapper mapper = Mappers.getMapper(VehicleApiMapper.class);

    @Test
    void toDomain() {
        final Vin vin = new Vin("1HGBH41JXMN109186");
        final VehicleRequest request = new VehicleRequest(vin);

        final Vehicle result = mapper.toDomain(request);

        final Vehicle expected = new Vehicle(null, vin);
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void toDomainWithId() {
        final UUID id = UUID.randomUUID();
        final Vin vin = new Vin("1HGBH41JXMN109186");
        final VehicleRequest request = new VehicleRequest(vin);

        final Vehicle result = mapper.toDomain(id, request);

        final Vehicle expected = new Vehicle(id, vin);
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void toResponse() {
        final UUID id = UUID.randomUUID();
        final Vin vin = new Vin("1HGBH41JXMN109186");
        final Vehicle vehicle = new Vehicle(id, vin);

        final VehicleResponse result = mapper.toResponse(vehicle);

        final VehicleResponse expected = new VehicleResponse(id, vin);
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }
}
