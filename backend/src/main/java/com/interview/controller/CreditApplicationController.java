package com.interview.controller;

import java.net.URI;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.interview.persistence.enums.ApplicationStatus;
import com.interview.dto.request.CreateCreditApplicationRequest;
import com.interview.dto.request.UpdateApplicationStatusRequest;
import com.interview.dto.response.CreditApplicationResponse;
import com.interview.service.CreditApplicationService;

@RestController
@RequestMapping("/api/v1/credit-applications")
@RequiredArgsConstructor
@Tag(name = "Credit Applications", description = "Submit and manage customer credit applications with state-machine status transitions")
public class CreditApplicationController {

    private final CreditApplicationService creditApplicationService;

    @PostMapping
    @Operation(summary = "Submit a new credit application")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Application submitted"),
        @ApiResponse(responseCode = "400", description = "Validation error or loan amount exceeds limit"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<CreditApplicationResponse> create(
            @Valid @RequestBody final CreateCreditApplicationRequest request) {
        final CreditApplicationResponse response = creditApplicationService.create(request);
        final URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.getId()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a credit application by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Application found"),
        @ApiResponse(responseCode = "404", description = "Application not found")
    })
    public ResponseEntity<CreditApplicationResponse> findById(@PathVariable final UUID id) {
        return ResponseEntity.ok(creditApplicationService.findById(id));
    }

    @GetMapping
    @Operation(summary = "List all credit applications (paginated, optional status filter)")
    @ApiResponse(responseCode = "200", description = "Paginated list of applications")
    public ResponseEntity<Page<CreditApplicationResponse>> findAll(
            @Parameter(description = "Filter by status (optional)")
            @RequestParam(required = false) final ApplicationStatus status,
            @PageableDefault(size = 20, sort = "submittedAt", direction = Sort.Direction.DESC) final Pageable pageable) {
        return ResponseEntity.ok(creditApplicationService.findAll(status, pageable));
    }

    @GetMapping("/customers/{customerId}")
    @Operation(summary = "Get all applications submitted by a customer")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Paginated list of applications"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<Page<CreditApplicationResponse>> findByCustomer(
            @PathVariable final UUID customerId,
            @PageableDefault(size = 20, sort = "submittedAt", direction = Sort.Direction.DESC) final Pageable pageable) {
        return ResponseEntity.ok(creditApplicationService.findByCustomerId(customerId, pageable));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Transition application status",
               description = "Valid transitions: SUBMITTED→UNDER_REVIEW, UNDER_REVIEW→APPROVED|DENIED")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Status updated"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "404", description = "Application not found"),
        @ApiResponse(responseCode = "409", description = "Invalid status transition")
    })
    public ResponseEntity<CreditApplicationResponse> updateStatus(
            @PathVariable final UUID id,
            @Valid @RequestBody final UpdateApplicationStatusRequest request) {
        return ResponseEntity.ok(creditApplicationService.update(id, request));
    }

    @PostMapping("/{id}/confirm-documents")
    @Operation(summary = "Confirm all documents have been uploaded",
               description = "Verifies each expected document exists in S3 via HeadObject. Call this after completing all presigned PUT uploads.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "All documents confirmed in S3"),
        @ApiResponse(responseCode = "404", description = "Application not found"),
        @ApiResponse(responseCode = "422", description = "One or more documents have not been uploaded yet")
    })
    public ResponseEntity<CreditApplicationResponse> confirmDocuments(@PathVariable final UUID id) {
        return ResponseEntity.ok(creditApplicationService.confirmDocumentsUploaded(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a credit application")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Application deleted"),
        @ApiResponse(responseCode = "404", description = "Application not found")
    })
    public ResponseEntity<Void> delete(@PathVariable final UUID id) {
        creditApplicationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
