package com.interview.controller.payloads;

import com.interview.entity.Inventory;
import com.interview.enums.InventoryStatus;
import com.interview.enums.InventoryType;
import lombok.Data;

@Data
public class InventoryResponsePayload {

    private Long id;

    private InventoryType type;

    private boolean status;

    private String brand;

    private String partName;

    private String partNumber;

    private Integer quantity;

    private String supportEmail;

    public InventoryResponsePayload(Inventory inventory) {
        this.id = inventory.getId();
        this.type = inventory.getType();
        this.status = inventory.getStatus().isActive();
        this.brand = inventory.getBrand();
        this.partName = inventory.getPartName();
        this.partNumber = inventory.getPartNumber();
        this.quantity = inventory.getQuantity();
        this.supportEmail = inventory.getSupportEmail();
    }
}
