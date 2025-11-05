package com.interview.model;

import com.interview.entity.WorkOrderEntity;
import com.interview.entity.WorkOrderPartEntity;
import com.interview.entity.PartEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.ArrayList;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Accessors(chain = true)
public class WorkOrder {
    private Long id;
    private List<PartRequirement> partRequirements;
    private boolean readyToStart;

    public WorkOrderEntity toEntity() {
        WorkOrderEntity entity = new WorkOrderEntity();
        entity.setId(this.id);

        if (this.partRequirements != null && !this.partRequirements.isEmpty()) {
            List<WorkOrderPartEntity> workOrderParts = new ArrayList<>();
            for (PartRequirement partRequirement : this.partRequirements) {
                WorkOrderPartEntity wop = new WorkOrderPartEntity()
                        .setWorkOrder(entity)
                        .setPart(new PartEntity().setId(partRequirement.getPartId()))
                        .setPartCount(partRequirement.getRequiredCount());

                workOrderParts.add(wop);
            }
            entity.setParts(workOrderParts);
        } else {
            entity.setParts(new ArrayList<>());
        }
        return entity;
    }

    public static WorkOrder fromEntity(WorkOrderEntity entity) {
        WorkOrder model = new WorkOrder()
                .setId(entity.getId())
                .setReadyToStart(isReadyToStart(entity));

        if (entity.getParts() != null && !entity.getParts().isEmpty()) {
            List<PartRequirement> partRequirements = new ArrayList<>();
            for (WorkOrderPartEntity wop : entity.getParts()) {
                PartRequirement partRequirement = new PartRequirement()
                        .setPartId(wop.getPart().getId())
                        .setRequiredCount(wop.getPartCount());
                partRequirements.add(partRequirement);
            }
            model.partRequirements = partRequirements;
        }

        return model;
    }

    protected static boolean isReadyToStart(WorkOrderEntity workOrder) {
        // Work order is "ready to start" if there is sufficient inventory for all required parts
        return workOrder.getParts().stream()
                .allMatch(wop -> wop.getPart().getInventory() >= wop.getPartCount());
    }
}
