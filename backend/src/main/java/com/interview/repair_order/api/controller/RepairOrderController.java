package com.interview.repair_order.api.controller;

import com.interview._infrastructure.domain.model.CustomError;
import com.interview.repair_order.api.model.RepairOrderRequest;
import com.interview.repair_order.api.model.RepairOrderResponse;
import com.interview.repair_order.service.RepairOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@AllArgsConstructor
@SecurityRequirement(name = "apiKeyAuth")
@ApiResponses(value = {
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = @Content(schema = @Schema(implementation = CustomError.class))),
})
@RequestMapping()
public class RepairOrderController {

    private static final String BASE_V0 = "/api/v0/repair-orders";
    private static final int PAGE_SIZE = 20;

    private RepairOrderService repairOrderService;

    @Operation(summary = "Create a new Repair order", description = "Creates a new repair order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(implementation = CustomError.class)))
    })
    @PostMapping(BASE_V0)
    public ResponseEntity<RepairOrderResponse> create(@Valid @RequestBody RepairOrderRequest request) {

        RepairOrderResponse response = repairOrderService.createRepairOrder(request);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path(BASE_V0 + "/{id}")
                .buildAndExpand(response.getId()).toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping(BASE_V0)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success")})
    public Page<RepairOrderResponse> getAll(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);

        return repairOrderService.getAllPaginated(pageable);
    }

    @Operation(summary = "Find a repair order by ID", description = "Returns details for a single repair order ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Repair Order not found",
                    content = @Content(schema = @Schema(implementation = CustomError.class)))})
    @GetMapping(BASE_V0 + "/{id}")
    public RepairOrderResponse getRepairOrder(
            @Parameter(description = "ID of the repair order. Cannot be empty.", required = true) @PathVariable String id) {
        return repairOrderService.getRepairOrder(id);
    }

    @Operation(summary = "Update a Repair Order", description = "Updates a repair order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(implementation = CustomError.class))),
            @ApiResponse(responseCode = "404", description = "Repair Order not found",
                    content = @Content(schema = @Schema(implementation = CustomError.class)))})

    @PutMapping(BASE_V0 + "/{id}")
    public RepairOrderResponse updateRepairOrder(
            @Parameter(description = "ID of the repair order. Cannot be empty.", required = true) @PathVariable String id,
            @Parameter(description = "Requested values for Repair Order", required = true)
            @Valid @RequestBody RepairOrderRequest repairOrderRequest) {
        return repairOrderService.updateRepairOrder(id, repairOrderRequest);
    }

    @Operation(summary = "Delete a Repair Order", description = "Deletes a repair order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "404", description = "Repair Order not found",
                    content = @Content(schema = @Schema(implementation = CustomError.class)))})
    @DeleteMapping(BASE_V0 + "/{id}")
    public ResponseEntity<Void> deleteRepairOrder(
            @Parameter(description = "ID of the repair order. Cannot be empty.", required = true) @PathVariable String id) {
        repairOrderService.deleteRepairOrder(id);

        return ResponseEntity.noContent().build();
    }
}
