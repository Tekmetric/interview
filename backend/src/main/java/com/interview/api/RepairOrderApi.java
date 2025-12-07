package com.interview.api;

import com.interview.dto.repairorder.CreateRepairOrderRequest;
import com.interview.dto.repairorder.RepairOrderDto;
import com.interview.dto.repairorder.UpdateRepairOrderRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

@Tag(name = "Repair Order API", description = "API for managing repair orders")
public interface RepairOrderApi {

    @Operation(summary = "Creates a new repair order", responses = {
            @ApiResponse(responseCode = "201", description = "Repair order created"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    ResponseEntity<RepairOrderDto> create(CreateRepairOrderRequest createRepairOrderRequest);

    @Operation(summary = "Gets a repair order by id", responses = {
            @ApiResponse(responseCode = "200", description = "Repair order found"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Repair order not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))

    })
    ResponseEntity<RepairOrderDto> getById(long repairOrderId);

    @Operation(summary = "Gets repair orders paginated", responses = {
            @ApiResponse(responseCode = "200", description = "Repair order found"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    ResponseEntity<PagedModel<RepairOrderDto>> getAll(@ParameterObject Pageable pageable);

    @Operation(summary = "Updates a repair order", responses = {
            @ApiResponse(responseCode = "200", description = "Repair order updated"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    ResponseEntity<RepairOrderDto> update(long repairOrderId, UpdateRepairOrderRequest updateRepairOrderRequest);

    @Operation(summary = "Deletes a repair order by id", responses = {
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    ResponseEntity<Void> deleteById(long repairOrderId);

}
