package com.interview.workorder.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import com.interview.customer.entity.Customer;
import com.interview.workorder.entity.WorkOrder;
import com.interview.workorder.model.WorkOrderStatus;
import com.interview.workorder.dto.WorkOrderRequest;
import com.interview.workorder.dto.WorkOrderResponse;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class WorkOrderMapperUnitTest {

    private final WorkOrderMapper mapper = Mappers.getMapper(WorkOrderMapper.class);

    @Test
    void toEntityShouldMapRequestFieldsAndIgnoreManagedFields() {
        WorkOrderRequest request = new WorkOrderRequest(
                "1HGCM82633A004352",
                "Brake pads replacement",
                WorkOrderStatus.OPEN
        );

        WorkOrder entity = mapper.toEntity(request);

        assertThat(entity.getVin()).isEqualTo("1HGCM82633A004352");
        assertThat(entity.getIssueDescription()).isEqualTo("Brake pads replacement");
        assertThat(entity.getStatus()).isEqualTo(WorkOrderStatus.OPEN);
        assertThat(entity.getId()).isNull();
        assertThat(entity.getCustomer()).isNull();
        assertThat(entity.getCreatedAt()).isNull();
        assertThat(entity.getUpdatedAt()).isNull();
        assertThat(entity.getVersion()).isNull();
    }

    @Test
    void toResponseShouldMapCustomerId() {
        Customer customer = new Customer();
        customer.setId(10L);
        customer.setName("Alice Johnson");

        WorkOrder workOrder = new WorkOrder();
        workOrder.setId(99L);
        workOrder.setCustomer(customer);
        workOrder.setVin("JH4KA9650MC012345");
        workOrder.setIssueDescription("Engine check");
        workOrder.setStatus(WorkOrderStatus.IN_PROGRESS);
        workOrder.setCreatedAt(LocalDateTime.of(2026, 3, 4, 10, 0));
        workOrder.setUpdatedAt(LocalDateTime.of(2026, 3, 4, 11, 0));

        WorkOrderResponse response = mapper.toResponse(workOrder);

        assertThat(response.id()).isEqualTo(99L);
        assertThat(response.customerId()).isEqualTo(10L);
        assertThat(response.vin()).isEqualTo("JH4KA9650MC012345");
        assertThat(response.issueDescription()).isEqualTo("Engine check");
        assertThat(response.status()).isEqualTo(WorkOrderStatus.IN_PROGRESS);
        assertThat(response.createdAt()).isEqualTo(LocalDateTime.of(2026, 3, 4, 10, 0));
        assertThat(response.updatedAt()).isEqualTo(LocalDateTime.of(2026, 3, 4, 11, 0));
    }

    @Test
    void updateEntityFromRequestShouldUpdateOnlyMutableFields() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("Seed Customer");

        WorkOrder existing = new WorkOrder();
        existing.setId(5L);
        existing.setCustomer(customer);
        existing.setVin("1HGCM82633A004352");
        existing.setIssueDescription("Initial issue");
        existing.setStatus(WorkOrderStatus.OPEN);
        existing.setCreatedAt(LocalDateTime.of(2026, 1, 1, 10, 0));
        existing.setUpdatedAt(LocalDateTime.of(2026, 1, 1, 10, 0));
        existing.setVersion(3L);

        WorkOrderRequest request = new WorkOrderRequest(
                "1HGCM82633A004352",
                "Issue resolved",
                WorkOrderStatus.COMPLETED
        );

        mapper.updateEntityFromRequest(request, existing);

        assertThat(existing.getVin()).isEqualTo("1HGCM82633A004352");
        assertThat(existing.getIssueDescription()).isEqualTo("Issue resolved");
        assertThat(existing.getStatus()).isEqualTo(WorkOrderStatus.COMPLETED);
        assertThat(existing.getId()).isEqualTo(5L);
        assertThat(existing.getCustomer()).isSameAs(customer);
        assertThat(existing.getCreatedAt()).isEqualTo(LocalDateTime.of(2026, 1, 1, 10, 0));
        assertThat(existing.getUpdatedAt()).isEqualTo(LocalDateTime.of(2026, 1, 1, 10, 0));
        assertThat(existing.getVersion()).isEqualTo(3L);
    }
}
