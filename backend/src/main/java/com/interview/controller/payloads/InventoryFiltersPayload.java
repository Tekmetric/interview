package com.interview.controller.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryFiltersPayload {

    private String partName;

    private String partNumber;

    private String brand;

    private String supportEmail;

    private Boolean status;

    public boolean isFiltersEmpty() {
        return this.getPartName() == null && this.getPartNumber() == null && this.getBrand() == null &&
                this.getSupportEmail() == null && this.getStatus() == null;
    }
}
