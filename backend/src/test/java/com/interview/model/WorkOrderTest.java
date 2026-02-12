package com.interview.model;

import com.interview.entity.PartEntity;
import com.interview.entity.WorkOrderEntity;
import com.interview.entity.WorkOrderPartEntity;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class WorkOrderTest {
    @Test
    void isReadyToStart_shouldReturnFalse_whenPartHasInsufficientInventory() {
        var workOrder = new WorkOrderEntity()
                .setParts(List.of(
                        new WorkOrderPartEntity()
                                .setPartCount(5)
                                .setPart(new PartEntity(1L, "foo", 6)),
                        new WorkOrderPartEntity()
                                .setPartCount(10)
                                .setPart(new PartEntity(2L, "bar", 20)),
                        new WorkOrderPartEntity()
                                .setPartCount(30)
                                .setPart(new PartEntity(3L, "baz", 29))
                ));

        assertThat(WorkOrder.isReadyToStart(workOrder)).isFalse();
    }

    @Test
    void isReadyToStart_shouldReturnTrue_whenPartHasSufficientInventory() {
        var workOrder = new WorkOrderEntity()
                .setParts(List.of(
                        new WorkOrderPartEntity()
                                .setPartCount(5)
                                .setPart(new PartEntity(1L, "foo", 6)),
                        new WorkOrderPartEntity()
                                .setPartCount(10)
                                .setPart(new PartEntity(2L, "bar", 20)),
                        new WorkOrderPartEntity()
                                .setPartCount(30)
                                .setPart(new PartEntity(3L, "baz", 31))
                ));

        assertThat(WorkOrder.isReadyToStart(workOrder)).isTrue();
    }
}