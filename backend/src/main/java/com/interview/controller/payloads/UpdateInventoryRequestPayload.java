package com.interview.controller.payloads;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.interview.enums.InventoryStatus;
import com.interview.enums.InventoryType;
import lombok.Data;

@Data
public class UpdateInventoryRequestPayload {

    @JsonProperty("type")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private InventoryType type;

    @JsonProperty("status")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private InventoryStatus status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String brand;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String partName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String partNumber;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer quantity;

    public boolean isEmptyRequest() {
        return this.getType() == null && this.getStatus() == null && this.getBrand() == null &&
                this.getPartName() == null && this.getPartNumber() == null && this.getQuantity() == null;
    }
}
