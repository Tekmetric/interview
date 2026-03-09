package com.interview.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.interview.api.request.WorkOrderRequest;
import com.interview.api.response.LaborLineItemResponse;
import com.interview.api.response.PartLineItemResponse;
import com.interview.api.response.WorkOrderResponse;
import com.interview.api.response.WorkOrderSearchResponse;
import com.interview.domain.WorkOrder;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class WorkOrderApiMapperTest {

    private final WorkOrderApiMapper mapper = Mappers.getMapper(WorkOrderApiMapper.class);

    @Test
    void toDomain() {
        final UUID customerId = UUID.randomUUID();
        final UUID vehicleId = UUID.randomUUID();
        final Instant scheduledStart = Instant.parse("2026-04-01T09:00:00Z");
        final WorkOrderRequest request = new WorkOrderRequest(scheduledStart, customerId, vehicleId);

        final WorkOrder result = mapper.toDomain(request);

        final WorkOrder expected = new WorkOrder(null, scheduledStart, customerId, vehicleId);
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void toDomainWithId() {
        final UUID id = UUID.randomUUID();
        final UUID customerId = UUID.randomUUID();
        final UUID vehicleId = UUID.randomUUID();
        final Instant scheduledStart = Instant.parse("2026-04-01T09:00:00Z");
        final WorkOrderRequest request = new WorkOrderRequest(scheduledStart, customerId, vehicleId);

        final WorkOrder result = mapper.toDomain(id, request);

        final WorkOrder expected = new WorkOrder(id, scheduledStart, customerId, vehicleId);
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void toSearchResponse() {
        final UUID id = UUID.randomUUID();
        final UUID customerId = UUID.randomUUID();
        final UUID vehicleId = UUID.randomUUID();
        final Instant scheduledStart = Instant.parse("2026-04-01T09:00:00Z");
        final WorkOrder workOrder = new WorkOrder(id, scheduledStart, customerId, vehicleId);

        final WorkOrderSearchResponse result = mapper.toSearchResponse(workOrder);

        final WorkOrderSearchResponse expected = new WorkOrderSearchResponse(id, scheduledStart, customerId, vehicleId);
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void toResponse() {
        final UUID id = UUID.randomUUID();
        final UUID customerId = UUID.randomUUID();
        final UUID vehicleId = UUID.randomUUID();
        final Instant scheduledStart = Instant.parse("2026-04-01T09:00:00Z");
        final WorkOrder workOrder = new WorkOrder(id, scheduledStart, customerId, vehicleId);

        final List<PartLineItemResponse> parts =
                List.of(new PartLineItemResponse(UUID.randomUUID(), "Oil Filter", 1, UUID.randomUUID()));
        final List<LaborLineItemResponse> labors =
                List.of(new LaborLineItemResponse(UUID.randomUUID(), "Oil Change", 1, UUID.randomUUID()));

        final WorkOrderResponse result = mapper.toResponse(workOrder, parts, labors);

        assertThat(result.id()).isEqualTo(id);
        assertThat(result.scheduledStartDateTime()).isEqualTo(scheduledStart);
        assertThat(result.customerId()).isEqualTo(customerId);
        assertThat(result.vehicleId()).isEqualTo(vehicleId);
        assertThat(result.partLineItems()).containsExactlyElementsOf(parts);
        assertThat(result.laborLineItems()).containsExactlyElementsOf(labors);
    }
}
