package com.interview.controller.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.interview.enums.InventoryStatus;
import com.interview.enums.InventoryType;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class InsertInventoryRequestPayload {

    @NotNull
    @JsonProperty("type")
    private InventoryType type;

    @NotNull
    @JsonProperty("status")
    private InventoryStatus status;

    @NotNull
    private String brand;

    @NotNull
    private String partName;

    @NotNull
    private String partNumber;

    @NotNull
    private Integer quantity;
}
