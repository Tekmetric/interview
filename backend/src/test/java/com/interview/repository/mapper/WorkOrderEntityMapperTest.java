package com.interview.repository.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.interview.domain.WorkOrder;
import com.interview.repository.entity.CustomerEntity;
import com.interview.repository.entity.VehicleEntity;
import com.interview.repository.entity.WorkOrderEntity;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WorkOrderEntityMapperTest {

    @Mock
    EntityReferenceMapper entityReferenceMapper;

    @InjectMocks
    final WorkOrderEntityMapper mapper = Mappers.getMapper(WorkOrderEntityMapper.class);

    @Test
    void toEntity() {
        final UUID customerId = UUID.randomUUID();
        final UUID vehicleId = UUID.randomUUID();
        final Instant scheduledStart = Instant.parse("2026-04-01T09:00:00Z");
        final WorkOrder workOrder = new WorkOrder(UUID.randomUUID(), scheduledStart, customerId, vehicleId);

        final CustomerEntity customerRef = new CustomerEntity();
        customerRef.setId(customerId);
        final VehicleEntity vehicleRef = new VehicleEntity();
        vehicleRef.setId(vehicleId);
        when(entityReferenceMapper.toReference(eq(customerId), any())).thenReturn(customerRef);
        when(entityReferenceMapper.toReference(eq(vehicleId), any())).thenReturn(vehicleRef);

        final WorkOrderEntity result = mapper.toEntity(workOrder);

        final WorkOrderEntity expected = new WorkOrderEntity();
        expected.setScheduledStartDateTime(scheduledStart);
        expected.setCustomer(customerRef);
        expected.setVehicle(vehicleRef);
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("id", "partLineItems", "laborLineItems")
                .isEqualTo(expected);
        assertThat(result.getId()).isNotNull();
        assertThat(result.getPartLineItems()).isEmpty();
        assertThat(result.getLaborLineItems()).isEmpty();
    }

    @Test
    void updateEntity() {
        final UUID oldId = UUID.randomUUID();
        final UUID customerId = UUID.randomUUID();
        final UUID vehicleId = UUID.randomUUID();
        final WorkOrderEntity entity = new WorkOrderEntity();
        entity.setId(oldId);
        entity.setScheduledStartDateTime(Instant.parse("2026-03-01T08:00:00Z"));

        final CustomerEntity customerRef = new CustomerEntity();
        customerRef.setId(customerId);
        final VehicleEntity vehicleRef = new VehicleEntity();
        vehicleRef.setId(vehicleId);
        when(entityReferenceMapper.toReference(eq(customerId), any())).thenReturn(customerRef);
        when(entityReferenceMapper.toReference(eq(vehicleId), any())).thenReturn(vehicleRef);

        final Instant newScheduledStart = Instant.parse("2026-04-01T10:00:00Z");
        mapper.updateEntity(new WorkOrder(oldId, newScheduledStart, customerId, vehicleId), entity);

        final WorkOrderEntity expected = new WorkOrderEntity();
        expected.setId(oldId);
        expected.setScheduledStartDateTime(newScheduledStart);
        expected.setCustomer(customerRef);
        expected.setVehicle(vehicleRef);
        assertThat(entity)
                .usingRecursiveComparison()
                .ignoringFields("partLineItems", "laborLineItems")
                .isEqualTo(expected);
        assertThat(entity.getPartLineItems()).isEmpty();
        assertThat(entity.getLaborLineItems()).isEmpty();
    }

    @Test
    void toDomain() {
        final UUID id = UUID.randomUUID();
        final UUID customerId = UUID.randomUUID();
        final UUID vehicleId = UUID.randomUUID();
        final Instant scheduledStart = Instant.parse("2026-04-01T09:00:00Z");

        final CustomerEntity customer = new CustomerEntity();
        customer.setId(customerId);
        final VehicleEntity vehicle = new VehicleEntity();
        vehicle.setId(vehicleId);
        when(entityReferenceMapper.toId(customer)).thenReturn(customerId);
        when(entityReferenceMapper.toId(vehicle)).thenReturn(vehicleId);

        final WorkOrderEntity entity = new WorkOrderEntity();
        entity.setId(id);
        entity.setScheduledStartDateTime(scheduledStart);
        entity.setCustomer(customer);
        entity.setVehicle(vehicle);

        final WorkOrder result = mapper.toDomain(entity);

        final WorkOrder expected = new WorkOrder(id, scheduledStart, customerId, vehicleId);
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }
}
