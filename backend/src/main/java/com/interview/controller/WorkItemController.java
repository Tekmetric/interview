package com.interview.controller;

import com.interview.api.WorkItemApi;
import com.interview.dto.workitem.CreateWorkItemRequest;
import com.interview.dto.workitem.UpdateWorkItemRequest;
import com.interview.dto.workitem.WorkItemDto;
import com.interview.service.WorkItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/repair-orders/{repairOrderId}/items")
public class WorkItemController implements WorkItemApi {

    private final WorkItemService workItemService;

    @Override
    @PostMapping
    public ResponseEntity<WorkItemDto> create(@PathVariable("repairOrderId") long repairOrderId,
                                              @Valid @RequestBody CreateWorkItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workItemService.create(repairOrderId, request));
    }

    @Override
    @GetMapping
    public ResponseEntity<PagedModel<WorkItemDto>> getAll(@PathVariable("repairOrderId") long repairOrderId,
                                                          @PageableDefault(sort = "id") Pageable pageable) {
        return ResponseEntity.ok(new PagedModel<>(workItemService.getAll(repairOrderId, pageable)));
    }

    @Override
    @PutMapping("/{workItemId}")
    public ResponseEntity<WorkItemDto> update(@PathVariable("repairOrderId") long repairOrderId,
                                              @PathVariable("workItemId") long workItemId,
                                              @Valid @RequestBody UpdateWorkItemRequest updateRequest) {
        WorkItemDto updatedWorkItem = workItemService.update(repairOrderId, workItemId, updateRequest);
        return ResponseEntity.ok(updatedWorkItem);
    }

    @Override
    @DeleteMapping("/{workItemId}")
    public ResponseEntity<Void> deleteById(@PathVariable("repairOrderId") long repairOrderId,
                                           @PathVariable("workItemId") long workItemId) {
        workItemService.deleteByRepairOrderIdAndItemId(repairOrderId, workItemId);

        return ResponseEntity.noContent().build();
    }
}
