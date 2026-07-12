package com.interview.controller;

import com.interview.dto.CursorPageResponse;
import com.interview.dto.EventMapper;
import com.interview.dto.EventRequest;
import com.interview.dto.EventResponse;
import com.interview.dto.EventSummaryResponse;
import com.interview.model.Event;
import com.interview.model.EventDocument;
import com.interview.model.Performer;
import com.interview.model.Venue;
import com.interview.repository.PerformerRepository;
import com.interview.repository.VenueRepository;
import com.interview.service.EventSearchService;
import com.interview.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Events", description = "Event management APIs for concerts, sports, and tech events")
@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;
    private final EventSearchService searchService;
    private final EventMapper eventMapper;
    private final VenueRepository venueRepository;
    private final PerformerRepository performerRepository;

    @Autowired
    public EventController(EventService eventService,
                           EventSearchService searchService,
                           EventMapper eventMapper,
                          VenueRepository venueRepository, PerformerRepository performerRepository) {
        this.eventService = eventService;
        this.searchService = searchService;
        this.eventMapper = eventMapper;
        this.venueRepository = venueRepository;
        this.performerRepository = performerRepository;
    }

    /**
     * Helper method to check if the current user has ADMIN role
     */
    private boolean isCurrentUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }

/*@Operation(summary = "Get all events", description = "Retrieve a list of all events including concerts, sports, and tech events")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of events")
})
@GetMapping
public ResponseEntity<List<EventSummaryResponse>> getAllEvents() {
    boolean isAdmin = isCurrentUserAdmin();
    List<Event> events = eventService.getAllEvents();
    List<EventSummaryResponse> responses = events.stream()
            .map(event -> eventMapper.toSummaryResponse(event, isAdmin))
            .collect(Collectors.toList());
    return ResponseEntity.ok(responses);
}*/

    @Operation(summary = "Get events with cursor-based pagination",
               description = "Retrieve events using cursor-based pagination. Use 'cursor' parameter to get the next page, and 'pageSize' to specify the number of results per page. The response includes 'nextCursor' for fetching the next page and 'hasMore' to indicate if more results are available.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated list of events")
    })
    @GetMapping("/paginated")
    public ResponseEntity<CursorPageResponse<EventSummaryResponse>> getEventsPaginated(
            @Parameter(description = "Cursor for pagination (UUID of the last event from previous page). Omit for the first page.")
            @RequestParam(required = false) UUID cursor,
            @Parameter(description = "Number of events per page (default: 10, max: 100)")
            @RequestParam(defaultValue = "10") int pageSize) {

        boolean isAdmin = isCurrentUserAdmin();

        // Validate page size
        if (pageSize < 1 || pageSize > 100) {
            pageSize = 10;
        }

        CursorPageResponse<Event> eventPage = eventService.getEventsCursorPaginated(cursor, pageSize);

        // Map events to summary responses
        List<EventSummaryResponse> eventResponses = eventPage.getData().stream()
                .map(event -> eventMapper.toSummaryResponse(event, isAdmin))
                .collect(Collectors.toList());

        CursorPageResponse<EventSummaryResponse> response = new CursorPageResponse<>(
                eventResponses,
                eventPage.getNextCursor(),
                eventPage.isHasMore(),
                eventPage.getPageSize()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get event by ID", description = "Retrieve a specific event by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event found"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(
            @Parameter(description = "ID of the event to retrieve") @PathVariable UUID id) {
        boolean isAdmin = isCurrentUserAdmin();
        return eventService.getEventById(id)
                .map(event -> eventMapper.toResponse(event, isAdmin))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create new event", description = "Create a new event (concert, sports game, or tech event)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Event created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<EventResponse> createEvent(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Event request object")
            @Valid @RequestBody EventRequest request) {
        try {
            boolean isAdmin = isCurrentUserAdmin();
            Event event = eventMapper.toEntity(request);

            // Set venue if provided
            if (request.getVenueId() != null) {
                Venue venue = venueRepository.findById(request.getVenueId())
                        .orElseThrow(() -> new RuntimeException("Venue not found with id: " + request.getVenueId()));
                event.setVenue(venue);
            }

            // Set performers if provided
            if (request.getPerformerIds() != null && !request.getPerformerIds().isEmpty()) {
                Set<Performer> performers = new HashSet<>();
                for (UUID performerId : request.getPerformerIds()) {
                    Performer performer = performerRepository.findById(performerId)
                            .orElseThrow(() -> new RuntimeException("Performer not found with id: " + performerId));
                    performers.add(performer);
                }
                event.setPerformers(performers);
            }

            Event createdEvent = eventService.createEvent(event);
            return ResponseEntity.status(HttpStatus.CREATED).body(eventMapper.toResponse(createdEvent, isAdmin));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(summary = "Update event", description = "Update an existing event by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event updated successfully"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<EventResponse> updateEvent(
            @Parameter(description = "ID of the event to update") @PathVariable UUID id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Updated event request object")
            @Valid @RequestBody EventRequest request) {
        try {
            boolean isAdmin = isCurrentUserAdmin();
            Event event = eventService.getEventById(id)
                    .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));

            // Update basic fields
            eventMapper.updateEntityFromRequest(event, request);

            // Update venue if provided
            if (request.getVenueId() != null) {
                Venue venue = venueRepository.findById(request.getVenueId())
                        .orElseThrow(() -> new RuntimeException("Venue not found with id: " + request.getVenueId()));
                event.setVenue(venue);
            } else {
                event.setVenue(null);
            }

            // Update performers if provided
            if (request.getPerformerIds() != null) {
                Set<Performer> performers = new HashSet<>();
                for (UUID performerId : request.getPerformerIds()) {
                    Performer performer = performerRepository.findById(performerId)
                            .orElseThrow(() -> new RuntimeException("Performer not found with id: " + performerId));
                    performers.add(performer);
                }
                event.setPerformers(performers);
            } else {
                event.setPerformers(new HashSet<>());
            }

            Event updatedEvent = eventService.createEvent(event);
            return ResponseEntity.ok(eventMapper.toResponse(updatedEvent, isAdmin));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete event", description = "Delete an event by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Event deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(
            @Parameter(description = "ID of the event to delete") @PathVariable UUID id) {
        try {
            eventService.deleteEvent(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

//    // Search events by name
//    @GetMapping("/search")
//    public ResponseEntity<List<EventResponse>> searchEventsByName(@RequestParam String name) {
//        List<Event> events = eventService.searchEventsByName(name);
//        List<EventResponse> responses = events.stream()
//                .map(eventMapper::toResponse)
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(responses);
//    }

    // Get events by location
    @GetMapping("/location/{location}")
    public ResponseEntity<List<EventSummaryResponse>> getEventsByLocation(@PathVariable String location) {
        boolean isAdmin = isCurrentUserAdmin();
        List<Event> events = eventService.getEventsByLocation(location);
        List<EventSummaryResponse> responses = events.stream()
                .map(event -> eventMapper.toSummaryResponse(event, isAdmin))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // Get upcoming events
    @GetMapping("/upcoming")
    public ResponseEntity<List<EventSummaryResponse>> getUpcomingEvents() {
        boolean isAdmin = isCurrentUserAdmin();
        List<Event> events = eventService.getUpcomingEvents();
        List<EventSummaryResponse> responses = events.stream()
                .map(event -> eventMapper.toSummaryResponse(event, isAdmin))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // Get events between dates
    @GetMapping("/between")
    public ResponseEntity<List<EventSummaryResponse>> getEventsBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        boolean isAdmin = isCurrentUserAdmin();
        List<Event> events = eventService.getEventsBetweenDates(startDate, endDate);
        List<EventSummaryResponse> responses = events.stream()
                .map(event -> eventMapper.toSummaryResponse(event, isAdmin))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

     // OpenSearch search endpoints
     @Operation(summary = "Search events using OpenSearch",
                description = "Search events by query string across name, description, and location fields using OpenSearch full-text search")
     @GetMapping("/search")
     public ResponseEntity<List<EventDocument>> search(
             @RequestParam(required = false) String keyword,
             @RequestParam(required = false) String city,
             @RequestParam(defaultValue = "1") int page,
             @RequestParam(defaultValue = "20") int size
     ) throws Exception {
         List<EventDocument> results = searchService.search(keyword, city, page, size);
         return ResponseEntity.ok(results);
     }

     @Operation(summary = "Index all events to OpenSearch",
                description = "Index all events from the database to OpenSearch for full-text search capabilities")
     @PreAuthorize("hasRole('ADMIN')")
     @PostMapping("/search/index")
     public ResponseEntity<String> indexAllEvents() {
         try {
             searchService.indexAllEvents();
             return ResponseEntity.ok("All events indexed successfully");
         } catch (Exception e) {
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                     .body("Error indexing events: " + e.getMessage());
         }
     }
}