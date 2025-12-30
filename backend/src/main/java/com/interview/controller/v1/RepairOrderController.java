package com.interview.controller.v1;

import com.interview.dto.PageResponse;
import com.interview.dto.RepairOrderCreateRequest;
import com.interview.dto.RepairOrderResponse;
import com.interview.dto.RepairOrderUpdateRequest;
import com.interview.entity.RepairOrderStatus;
import com.interview.exception.ConflictException;
import com.interview.service.AuditService;
import com.interview.service.RepairOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1/repair-orders")
@Tag(
        name = "${api.repairOrders.tag}",
        description = "${api.repairOrders.tagDesc}"
)
public class RepairOrderController {

    private final RepairOrderService service;
    private final AuditService auditService;

    public RepairOrderController(RepairOrderService service, AuditService auditService) {
        this.service = service;
        this.auditService = auditService;
    }

    @Operation(
            summary = "${api.repairOrders.create.summary}",
            description = "${api.repairOrders.create.desc}"
    )
    @PostMapping
    @PreAuthorize("hasRole('SERVICE')")
    public ResponseEntity<RepairOrderResponse> create(@Valid @RequestBody RepairOrderCreateRequest req) {
        RepairOrderResponse created = service.create(req);
        return ResponseEntity.ok().eTag(etagOf(created.version())).body(created);
    }

    @Operation(
            summary = "${api.repairOrders.get.summary}",
            description = "${api.repairOrders.get.desc}"
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SERVICE','READONLY')")
    public ResponseEntity<RepairOrderResponse> get(@PathVariable("id") Long id) {
        RepairOrderResponse response = service.get(id);
        return ResponseEntity.ok().eTag(etagOf(response.version())).body(response);
    }

    @Operation(
            summary = "${api.repairOrders.list.summary}",
            description = "${api.repairOrders.list.desc}"
    )
    @GetMapping
    @PreAuthorize("hasAnyRole('SERVICE','READONLY')")
    public PageResponse<RepairOrderResponse> list(
            @ParameterObject @PageableDefault(size = 20, sort = "createdAt") Pageable pageable,
            @RequestParam(name = "status", required = false) RepairOrderStatus status,
            @RequestParam(name = "vinContains", required = false) String vinContains,
            @RequestParam(name = "make", required = false) String make,
            @RequestParam(name = "model", required = false) String model,
            @RequestParam(name = "from", required = false) OffsetDateTime from,
            @RequestParam(name = "to", required = false) OffsetDateTime to) {
        Instant fromInstant = from != null ? from.toInstant() : null;
        Instant toInstant = to != null ? to.toInstant() : null;
        return service.list(pageable, status, vinContains, make, model, fromInstant, toInstant);
    }

    @Operation(
            summary = "${api.repairOrders.update.summary}",
            description = "${api.repairOrders.update.desc}"
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SERVICE')")
    public ResponseEntity<RepairOrderResponse> update(@PathVariable("id") Long id,
                                                      @RequestHeader(value = "If-Match", required = false) String ifMatch,
                                                      @Valid @RequestBody RepairOrderUpdateRequest req) {
        requireIfMatch(ifMatch);
        service.verifyVersionHeader(id, ifMatch);
        RepairOrderResponse updated = service.update(id, req);
        return ResponseEntity.ok().eTag(etagOf(updated.version())).body(updated);
    }

    @Operation(
            summary = "${api.repairOrders.delete.summary}",
            description = "${api.repairOrders.delete.desc}"
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SERVICE')")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id,
                                       @RequestHeader(value = "If-Match", required = false) String ifMatch) {
        requireIfMatch(ifMatch);
        service.verifyVersionHeader(id, ifMatch);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "${api.repairOrders.audit.summary}",
            description = "${api.repairOrders.audit.desc}"
    )
    @PostMapping("/audit")
    @PreAuthorize("hasRole('SERVICE')")
    public void triggerAudit() { auditService.auditTotals(); }

    private String etagOf(Long version) { return "\"" + version + "\""; }

    private void requireIfMatch(String ifMatch) {
        if (ifMatch == null || ifMatch.isBlank()) {
            throw new ConflictException("Missing If-Match header. Provide the ETag value from the last GET response.");
        }
    }
}
