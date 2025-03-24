package com.interview.runningevents.infrastructure.web;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.interview.runningevents.application.exception.RunningEventNotFoundException;
import com.interview.runningevents.application.port.in.CreateRunningEventUseCase;
import com.interview.runningevents.application.port.in.GetRunningEventUseCase;
import com.interview.runningevents.domain.model.RunningEvent;
import com.interview.runningevents.infrastructure.web.dto.RunningEventDTOMapper;
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
    private final RunningEventDTOMapper dtoMapper;

    /**
     * Creates a new RunningEventController with the required dependencies.
     *
     * @param createRunningEventUseCase Use case for creating running events
     * @param getRunningEventUseCase Use case for retrieving running events
     * @param dtoMapper Mapper for converting between domain objects and DTOs
     */
    public RunningEventController(
            CreateRunningEventUseCase createRunningEventUseCase,
            GetRunningEventUseCase getRunningEventUseCase,
            RunningEventDTOMapper dtoMapper) {
        this.createRunningEventUseCase = createRunningEventUseCase;
        this.getRunningEventUseCase = getRunningEventUseCase;
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
}
