package com.interview.api;

import com.interview.dto.workitem.CreateWorkItemRequest;
import com.interview.dto.workitem.UpdateWorkItemRequest;
import com.interview.dto.workitem.WorkItemDto;
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

@Tag(name = "Work Item API", description = "API for managing work items attached to repair orders")
public interface WorkItemApi {

    @Operation(
            summary = "Creates a new work item for a given repair order",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Work item created"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Repair order not found",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))
                    )
            }
    )
    ResponseEntity<WorkItemDto> create(long repairOrderId, CreateWorkItemRequest request);


    @Operation(
            summary = "Gets paginated work items for a given Repair Order API",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Page returned successfully"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Repair order not found",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))
                    )
            }
    )
    ResponseEntity<PagedModel<WorkItemDto>> getAll(long repairOrderId, @ParameterObject Pageable pageable);

    @Operation(
            summary = "Updates a work item",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Work item updated"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Work item not found",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))
                    )
            }
    )
    ResponseEntity<WorkItemDto> update(long repairOrderId, long workItemId, UpdateWorkItemRequest updateRequest);

    @Operation(
            summary = "Deletes a work item",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Work item deleted"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Repair order or work item not found",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))
                    )
            }
    )
    ResponseEntity<Void> deleteById(long repairOrderId, long workItemId);
}
