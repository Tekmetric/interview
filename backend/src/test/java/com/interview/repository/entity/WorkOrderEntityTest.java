package com.interview.repository.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class WorkOrderEntityTest {

    @Test
    void addPartLineItemSetsWorkOrderAndAddsToCollection() {
        final WorkOrderEntity workOrder = new WorkOrderEntity();
        final PartLineItemEntity item = new PartLineItemEntity();

        workOrder.addPartLineItem(item);

        assertThat(workOrder.getPartLineItems()).containsExactly(item);
        assertThat(item.getWorkOrder()).isSameAs(workOrder);
    }

    @Test
    void removePartLineItemClearsWorkOrderAndRemovesFromCollection() {
        final WorkOrderEntity workOrder = new WorkOrderEntity();
        final PartLineItemEntity item = new PartLineItemEntity();
        workOrder.addPartLineItem(item);

        workOrder.removePartLineItem(item);

        assertThat(workOrder.getPartLineItems()).isEmpty();
        assertThat(item.getWorkOrder()).isNull();
    }

    @Test
    void addLaborLineItemSetsWorkOrderAndAddsToCollection() {
        final WorkOrderEntity workOrder = new WorkOrderEntity();
        final LaborLineItemEntity item = new LaborLineItemEntity();

        workOrder.addLaborLineItem(item);

        assertThat(workOrder.getLaborLineItems()).containsExactly(item);
        assertThat(item.getWorkOrder()).isSameAs(workOrder);
    }

    @Test
    void removeLaborLineItemClearsWorkOrderAndRemovesFromCollection() {
        final WorkOrderEntity workOrder = new WorkOrderEntity();
        final LaborLineItemEntity item = new LaborLineItemEntity();
        workOrder.addLaborLineItem(item);

        workOrder.removeLaborLineItem(item);

        assertThat(workOrder.getLaborLineItems()).isEmpty();
        assertThat(item.getWorkOrder()).isNull();
    }
}
