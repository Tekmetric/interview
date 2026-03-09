package com.interview.repository.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.interview.domain.Vehicle;
import com.interview.domain.Vin;
import com.interview.repository.entity.CustomerEntity;
import com.interview.repository.entity.VehicleEntity;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VehicleEntityMapperTest {

    @Mock
    EntityReferenceMapper entityReferenceMapper;

    @InjectMocks
    final VehicleEntityMapper mapper = Mappers.getMapper(VehicleEntityMapper.class);

    @Test
    void toEntity() {
        final UUID customerId = UUID.randomUUID();
        final Vin vin = new Vin("1HGBH41JXMN109186");
        final Vehicle vehicle = new Vehicle(UUID.randomUUID(), vin, customerId);

        final CustomerEntity customerRef = new CustomerEntity();
        customerRef.setId(customerId);
        when(entityReferenceMapper.toReference(eq(customerId), any())).thenReturn(customerRef);

        final VehicleEntity result = mapper.toEntity(vehicle);

        final VehicleEntity expected = new VehicleEntity();
        expected.setVin(vin);
        expected.setCustomer(customerRef);
        assertThat(result).usingRecursiveComparison().ignoringFields("id").isEqualTo(expected);
    }

    @Test
    void updateEntity() {
        final UUID oldId = UUID.randomUUID();
        final UUID customerId = UUID.randomUUID();
        final Vin oldVin = new Vin("2HGBH41JXMN109186");
        final VehicleEntity entity = new VehicleEntity();
        entity.setId(oldId);
        entity.setVin(oldVin);

        final CustomerEntity customerRef = new CustomerEntity();
        customerRef.setId(customerId);
        when(entityReferenceMapper.toReference(eq(customerId), any())).thenReturn(customerRef);

        final Vin newVin = new Vin("1HGBH41JXMN109186");
        mapper.updateEntity(new Vehicle(oldId, newVin, customerId), entity);

        assertThat(entity.getVin()).isEqualTo(newVin);
        assertThat(entity.getCustomer()).isEqualTo(customerRef);
        assertThat(entity.getId()).isEqualTo(oldId);
    }

    @Test
    void toDomain() {
        final UUID id = UUID.randomUUID();
        final UUID customerId = UUID.randomUUID();
        final Vin vin = new Vin("1HGBH41JXMN109186");

        final CustomerEntity customer = new CustomerEntity();
        customer.setId(customerId);
        when(entityReferenceMapper.toId(customer)).thenReturn(customerId);

        final VehicleEntity entity = new VehicleEntity();
        entity.setId(id);
        entity.setVin(vin);
        entity.setCustomer(customer);

        final Vehicle result = mapper.toDomain(entity);

        final Vehicle expected = new Vehicle(id, vin, customerId);
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }
}
