package com.interview.repair_order.api.model;

import com.interview.repair_order.domain.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class RepairOrderRequest {

    @NotNull
    @Schema(description = "The UUID for the related shopId")
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
            message = "must be a valid UUID")
    public String shopId;

    @NotNull
    @Size(max = 50, message = "must be less than 50 characters")
    @Schema(description = "A user visible repair order identifier")
    public String externalRO;

    @NotNull
    @Schema(description = "The status of the repair order")
    public Status status;

    @Schema(description = "The odometer reading upon intake.  If provided, must be less than or equal to odometerOut")
    public Integer odometerIn;

    @Schema(description = "The odometer reading at service end.  If provided, must be greater than or equal to odometerIn")
    public Integer odometerOut;

    @Schema(description = "Any notes to describe the issue.")
    @Size(max = 1000, message = "must be less than 1000 characters")
    public String notes;
}
