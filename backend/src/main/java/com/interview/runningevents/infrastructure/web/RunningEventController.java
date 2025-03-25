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
import com.interview.runningevents.application.exception.ValidationException;
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

import jakarta.validation.Valid;

/**
 * REST controller for managing running events.
 */
@RestController
@RequestMapping("/api/events")
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
     * @param getRunningEventUseCase Use case for retrieving running events
     * @param listRunningEventsUseCase Use case for listing running events
     * @param updateRunningEventUseCase Use case for updating running events
     * @param deleteRunningEventUseCase Use case for deleting running events
     * @param dtoMapper Mapper for converting between domain objects and DTOs
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
    public ResponseEntity<RunningEventResponseDTO> createRunningEvent(
            @Valid @RequestBody RunningEventRequestDTO requestDTO) {

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
    public ResponseEntity<RunningEventResponseDTO> getRunningEvent(@PathVariable Long id) {
        RunningEvent event =
                getRunningEventUseCase.getRunningEventById(id).orElseThrow(() -> new RunningEventNotFoundException(id));

        RunningEventResponseDTO responseDTO = dtoMapper.toResponseDTO(event);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Lists running events with optional filtering and pagination.
     *
     * @param startDate Optional minimum date for filtering events (inclusive)
     * @param endDate Optional maximum date for filtering events (inclusive)
     * @param page Page number (0-based, defaults to 0)
     * @param size Page size (defaults to 20)
     * @param sortBy Field to sort by (id, name, or dateTime; defaults to "dateTime")
     * @param sortDir Sort direction ("ASC" or "DESC", defaults to "ASC")
     * @return HTTP 200 OK with paginated list of events
     * @throws ValidationException if sort parameters are invalid
     */
    @GetMapping
    public ResponseEntity<PaginatedResponseDTO<RunningEventResponseDTO>> listRunningEvents(
            @RequestParam(required = false) Long startDate,
            @RequestParam(required = false) Long endDate,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false, defaultValue = "dateTime") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sortDir) {

        // Validate query parameters
        QueryParamValidator.validateSortField(sortBy);
        QueryParamValidator.validateSortDirection(sortDir);

        // Create query DTO from request parameters
        RunningEventQueryDTO queryDTO = RunningEventQueryDTO.builder()
                .fromDate(startDate)
                .toDate(endDate)
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
     * @param id The ID of the running event to update
     * @param requestDTO The updated running event data
     * @return HTTP 200 OK with the updated event data
     * @throws RunningEventNotFoundException If the event is not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<RunningEventResponseDTO> updateRunningEvent(
            @PathVariable Long id, @Valid @RequestBody RunningEventRequestDTO requestDTO) {

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
    public ResponseEntity<Void> deleteRunningEvent(@PathVariable Long id) {
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
