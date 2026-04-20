package com.interview.controller;

import com.interview.dto.CreateRepairOrderCommand;
import com.interview.dto.PageDto;
import com.interview.dto.RepairOrderDetailDto;
import com.interview.dto.RepairOrderSummaryDto;
import com.interview.dto.UpdateRepairOrderCommand;
import com.interview.exception.CustomerNotFoundException;
import com.interview.exception.RepairOrderNotFoundException;
import com.interview.exception.StaleVersionException;
import com.interview.service.RepairOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/repair-orders")
@RequiredArgsConstructor
@Tag(name = "Repair Orders", description = "Manage repair orders and their line items")
public class RepairOrderController {

    private final RepairOrderService repairOrderService;

    @PostMapping
    @Operation(
            summary = "Create a repair order",
            description = "Creates a new repair order with optional line items for an existing customer")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Repair order created",
                    headers = @io.swagger.v3.oas.annotations.headers.Header(
                            name = "Location", description = "URI of the created repair order")),
            @ApiResponse(responseCode = "400",
                    description = "Invalid request body",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404",
                    description = "Customer not found",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<RepairOrderDetailDto> create(
            @RequestBody CreateRepairOrderCommand command
    ) {
        log.info("POST /api/v1/repair-orders [customerId={}]", command.customerId());
        var detail = repairOrderService.create(command);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(detail.id())
                .toUri();
        return ResponseEntity.created(location).body(detail);
    }

    @GetMapping
    @Operation(
            summary = "List all repair orders",
            description = "Returns a paginated list of repair order summaries, sorted by the specified field and direction")
    @ApiResponse(responseCode = "200", description = "Paged list of repair order summaries")
    public PageDto<RepairOrderSummaryDto> findAll(
            @Parameter(description = "Zero-based page index") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size (1–100)") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Field to sort by", example = "createdAt") @RequestParam(defaultValue = "createdAt") String sort,
            @Parameter(description = "Sort direction", schema = @Schema(allowableValues = {"asc", "desc"}))
            @RequestParam(defaultValue = "desc") String direction
    ) {
        log.info("GET /api/v1/repair-orders [page={}, size={}, sort={}, direction={}]",
                page, size, sort, direction);
        return repairOrderService.findAll(page, size, sort, direction);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get repair order by ID",
            description = "Returns a single repair order with its line items")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Repair order found"),
            @ApiResponse(responseCode = "404",
                    description = "Repair order not found",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid UUID format",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    public RepairOrderDetailDto findById(
            @Parameter(description = "Repair order UUID")
            @PathVariable UUID id
    ) {
        log.info("GET /api/v1/repair-orders [id={}]", id);
        return repairOrderService.findById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a repair order",
            description = "Updates a repair order. Requires If-Match header with the current version for optimistic concurrency control")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Repair order updated"),
            @ApiResponse(responseCode = "400",
                    description = "Invalid request body or UUID format",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404",
                    description = "Repair order not found",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "412",
                    description = "Version mismatch — resource was modified since last read",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    public RepairOrderDetailDto update(
            @Parameter(description = "Repair order UUID") @PathVariable UUID id,
            @Parameter(description = "Expected version for optimistic locking")
            @RequestHeader("If-Match") int ifMatch,
            @RequestBody UpdateRepairOrderCommand command
    ) {
        log.info("PUT /api/v1/repair-orders [id={}, ifMatch={}]", id, ifMatch);
        return repairOrderService.update(id, ifMatch, command);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a repair order",
            description = "Deletes a repair order and its line items. Idempotent — returns 204 even if the order does not exist")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Repair order deleted"),
            @ApiResponse(responseCode = "400",
                    description = "Invalid UUID format",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    public void delete(
            @Parameter(description = "Repair order UUID")
            @PathVariable UUID id
    ) {
        log.info("DELETE /api/v1/repair-orders [id={}]", id);
        repairOrderService.delete(id);
    }

    @ExceptionHandler(RepairOrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleRepairOrderNotFound(RepairOrderNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleCustomerNotFound(CustomerNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(StaleVersionException.class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    public ProblemDetail handleStaleVersion(StaleVersionException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.PRECONDITION_FAILED,
                ex.getMessage());
    }
}
