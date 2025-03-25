package com.interview.runningevents.infrastructure.web;

import java.net.URI;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.interview.runningevents.application.exception.RunningEventNotFoundException;
import com.interview.runningevents.application.model.PaginatedResult;
import com.interview.runningevents.application.port.in.CreateRunningEventUseCase;
import com.interview.runningevents.application.port.in.DeleteRunningEventUseCase;
import com.interview.runningevents.application.port.in.GetRunningEventUseCase;
import com.interview.runningevents.application.port.in.ListRunningEventsUseCase;
import com.interview.runningevents.application.port.in.UpdateRunningEventUseCase;
import com.interview.runningevents.domain.model.RunningEvent;
import com.interview.runningevents.infrastructure.web.dto.PaginatedResponseDTO;
import com.interview.runningevents.infrastructure.web.dto.RunningEventDTOMapper;
import com.interview.runningevents.infrastructure.web.dto.RunningEventQueryDTO;
import com.interview.runningevents.infrastructure.web.dto.RunningEventRequestDTO;
import com.interview.runningevents.infrastructure.web.dto.RunningEventResponseDTO;
import com.interview.runningevents.infrastructure.web.validation.DateValidator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * REST controller for managing running events.
 */
@RestController
@RequestMapping("/api/events")
@Tag(name = "Running Events", description = "API for managing running events")
public class RunningEventController {

    private final CreateRunningEventUseCase createRunningEventUseCase;
    private final GetRunningEventUseCase getRunningEventUseCase;
    private final ListRunningEventsUseCase listRunningEventsUseCase;
    private final UpdateRunningEventUseCase updateRunningEventUseCase;
    private final DeleteRunningEventUseCase deleteRunningEventUseCase;
    private final RunningEventDTOMapper dtoMapper;

    /**
     * Creates a new RunningEventController with the required dependencies.
     *
     * @param createRunningEventUseCase Use case for creating running events
     * @param getRunningEventUseCase    Use case for retrieving running events
     * @param listRunningEventsUseCase  Use case for listing running events
     * @param updateRunningEventUseCase Use case for updating running events
     * @param deleteRunningEventUseCase Use case for deleting running events
     * @param dtoMapper                 Mapper for converting between domain objects and DTOs
     */
    public RunningEventController(
            CreateRunningEventUseCase createRunningEventUseCase,
            GetRunningEventUseCase getRunningEventUseCase,
            ListRunningEventsUseCase listRunningEventsUseCase,
            UpdateRunningEventUseCase updateRunningEventUseCase,
            DeleteRunningEventUseCase deleteRunningEventUseCase,
            RunningEventDTOMapper dtoMapper) {
        this.createRunningEventUseCase = createRunningEventUseCase;
        this.getRunningEventUseCase = getRunningEventUseCase;
        this.listRunningEventsUseCase = listRunningEventsUseCase;
        this.updateRunningEventUseCase = updateRunningEventUseCase;
        this.deleteRunningEventUseCase = deleteRunningEventUseCase;
        this.dtoMapper = dtoMapper;
    }

    /**
     * Creates a new running event.
     *
     * @param requestDTO The running event data to create
     * @return HTTP 201 Created with the created event data and location header
     */
    @PostMapping
    @Operation(
            summary = "Create a new running event",
            description = "Creates a new running event with the provided data")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Event created successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = RunningEventResponseDTO.class))),
                @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
            })
    public ResponseEntity<RunningEventResponseDTO> createRunningEvent(
            @Valid @RequestBody RunningEventRequestDTO requestDTO) {

        // Validate that the date is in the future
        DateValidator.validateFutureDate(requestDTO.getDateTime());

        // Convert DTO to domain model
        RunningEvent eventToCreate = dtoMapper.toDomain(requestDTO);

        // Create the event using the use case
        RunningEvent createdEvent = createRunningEventUseCase.createRunningEvent(eventToCreate);

        // Convert created domain model back to response DTO
        RunningEventResponseDTO responseDTO = dtoMapper.toResponseDTO(createdEvent);

        // Build the location URI for the created resource
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdEvent.getId())
                .toUri();

        // Return 201 Created with the created event and location header
        return ResponseEntity.created(location).body(responseDTO);
    }

    /**
     * Retrieves a specific running event by its ID.
     *
     * @param id The ID of the running event to retrieve
     * @return HTTP 200 OK with the event data
     * @throws RunningEventNotFoundException If the event is not found
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a running event by ID", description = "Retrieves a specific running event by its ID")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved the event",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = RunningEventResponseDTO.class))),
                @ApiResponse(responseCode = "404", description = "Event not found", content = @Content)
            })
    public ResponseEntity<RunningEventResponseDTO> getRunningEvent(
            @Parameter(description = "ID of the running event to retrieve", required = true) @PathVariable Long id) {
        RunningEvent event =
                getRunningEventUseCase.getRunningEventById(id).orElseThrow(() -> new RunningEventNotFoundException(id));

        RunningEventResponseDTO responseDTO = dtoMapper.toResponseDTO(event);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Lists running events with optional filtering and pagination.
     *
     * @param fromDate Optional minimum date for filtering events in format yyyy-MM-dd HH:mm
     * @param toDate   Optional maximum date for filtering events in format yyyy-MM-dd HH:mm
     * @param page     Page number (0-based, defaults to 0)
     * @param size     Page size (defaults to 20)
     * @param sortBy   Field to sort by (id, name, or dateTime; defaults to "dateTime")
     * @param sortDir  Sort direction ("ASC" or "DESC", defaults to "ASC")
     * @return HTTP 200 OK with paginated list of events
     */
    @GetMapping
    @Operation(
            summary = "List running events",
            description = "Retrieves a list of running events with filtering and pagination options")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved the events",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = PaginatedResponseDTO.class))),
                @ApiResponse(responseCode = "400", description = "Invalid query parameters", content = @Content)
            })
    public ResponseEntity<PaginatedResponseDTO<RunningEventResponseDTO>> listRunningEvents(
            @Parameter(description = "Minimum date (yyyy-MM-ddTHH:mm) for filtering events")
                    @RequestParam(required = false)
                    String fromDate,
            @Parameter(description = "Maximum date (yyyy-MM-ddTHH:mm) for filtering events")
                    @RequestParam(required = false)
                    String toDate,
            @Parameter(description = "Page number (0-based)") @RequestParam(required = false, defaultValue = "0")
                    Integer page,
            @Parameter(description = "Page size") @RequestParam(required = false, defaultValue = "20") Integer size,
            @Parameter(description = "Field to sort by (id, name, or dateTime)")
                    @RequestParam(required = false, defaultValue = "dateTime")
                    String sortBy,
            @Parameter(description = "Sort direction (ASC or DESC)")
                    @RequestParam(required = false, defaultValue = "ASC")
                    String sortDir) {

        // Validate query parameters
        QueryParamValidator.validateSortField(sortBy);
        QueryParamValidator.validateSortDirection(sortDir);

        // Validate date range if provided
        DateValidator.validateDateRange(fromDate, toDate);

        // Create query DTO from request parameters
        RunningEventQueryDTO queryDTO = RunningEventQueryDTO.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .page(page)
                .pageSize(size)
                .sortBy(sortBy)
                .sortDirection(sortDir)
                .build();

        // Convert to domain query model
        var queryModel = dtoMapper.toQueryModel(queryDTO);

        // Execute the query using the use case
        PaginatedResult<RunningEvent> result = listRunningEventsUseCase.listRunningEvents(queryModel);

        // Convert to response DTO
        PaginatedResponseDTO<RunningEventResponseDTO> responseDTO = dtoMapper.toPaginatedResponseDTO(result);

        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Updates an existing running event.
     *
     * @param id         The ID of the running event to update
     * @param requestDTO The updated running event data
     * @return HTTP 200 OK with the updated event data
     * @throws RunningEventNotFoundException If the event is not found
     */
    @PutMapping("/{id}")
    @Operation(
            summary = "Update a running event",
            description = "Updates an existing running event with the provided data")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Event updated successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = RunningEventResponseDTO.class))),
                @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
                @ApiResponse(responseCode = "404", description = "Event not found", content = @Content)
            })
    public ResponseEntity<RunningEventResponseDTO> updateRunningEvent(
            @Parameter(description = "ID of the running event to update", required = true) @PathVariable Long id,
            @Valid @RequestBody RunningEventRequestDTO requestDTO) {

        // Validate that the date is in the future
        DateValidator.validateFutureDate(requestDTO.getDateTime());

        // First check if the event exists
        getRunningEventUseCase.getRunningEventById(id).orElseThrow(() -> new RunningEventNotFoundException(id));

        // Convert DTO to domain model and set the ID
        RunningEvent eventToUpdate = dtoMapper.toDomain(requestDTO);
        eventToUpdate.setId(id);

        // Update the event using the use case
        Optional<RunningEvent> updatedEventOptional = updateRunningEventUseCase.updateRunningEvent(eventToUpdate);

        // This should never be empty since we checked existence above, but just to be safe
        RunningEvent updatedEvent = updatedEventOptional.orElseThrow(() -> new RunningEventNotFoundException(id));

        // Convert updated domain model back to response DTO
        RunningEventResponseDTO responseDTO = dtoMapper.toResponseDTO(updatedEvent);

        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Deletes a running event.
     *
     * @param id The ID of the running event to delete
     * @return HTTP 204 No Content on successful deletion
     * @throws RunningEventNotFoundException If the event is not found
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a running event", description = "Deletes a running event by its ID")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "204", description = "Event deleted successfully", content = @Content),
                @ApiResponse(responseCode = "404", description = "Event not found", content = @Content)
            })
    public ResponseEntity<Void> deleteRunningEvent(
            @Parameter(description = "ID of the running event to delete", required = true) @PathVariable Long id) {
        // Try to delete the event
        boolean deleted = deleteRunningEventUseCase.deleteRunningEvent(id);

        // If the deletion was not successful (event not found), throw an exception
        if (!deleted) {
            throw new RunningEventNotFoundException(id);
        }

        // Return 204 No Content for successful deletion
        return ResponseEntity.noContent().build();
    }
}
